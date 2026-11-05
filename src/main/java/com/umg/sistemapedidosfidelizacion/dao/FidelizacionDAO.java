package com.umg.sistemapedidosfidelizacion.dao;

import com.umg.sistemapedidosfidelizacion.config.ConexionDB;
import com.umg.sistemapedidosfidelizacion.excepcion.PuntosInsuficientesException;
import com.umg.sistemapedidosfidelizacion.modelo.Canje;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;
import com.umg.sistemapedidosfidelizacion.modelo.Menu;
import com.umg.sistemapedidosfidelizacion.modelo.MovimientoPuntos;
import com.umg.sistemapedidosfidelizacion.modelo.Producto;
import com.umg.sistemapedidosfidelizacion.modelo.Recompensa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FidelizacionDAO {

    public int acreditarPuntos(
            Connection conexion,
            int idCliente,
            int puntos,
            int idPedido
    ) throws SQLException {

        if (puntos <= 0) {
            return obtenerSaldo(conexion, idCliente);
        }

        String sqlSaldo = """
                SELECT saldo_puntos
                FROM clientes
                WHERE id_cliente = ?
                  AND activo = TRUE
                FOR UPDATE
                """;

        int saldoActual;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlSaldo)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "El cliente no existe o está desactivado."
                    );
                }

                saldoActual =
                        rs.getInt("saldo_puntos");
            }
        }

        int nuevoSaldo =
                saldoActual + puntos;

        String sqlActualizar = """
                UPDATE clientes
                SET saldo_puntos = ?
                WHERE id_cliente = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlActualizar)) {

            ps.setInt(1, nuevoSaldo);
            ps.setInt(2, idCliente);

            ps.executeUpdate();
        }

        String sqlMovimiento = """
                INSERT INTO movimientos_puntos
                (
                    id_cliente,
                    tipo,
                    puntos,
                    saldo_resultante,
                    id_pedido
                )
                VALUES (?, 'ACUMULACION', ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlMovimiento)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, puntos);
            ps.setInt(3, nuevoSaldo);
            ps.setInt(4, idPedido);

            ps.executeUpdate();
        }

        return nuevoSaldo;
    }

    public int obtenerSaldoPorCodigo(String codigo)
            throws SQLException {

        String sql = """
                SELECT saldo_puntos
                FROM clientes
                WHERE codigo = ?
                  AND activo = TRUE
                """;

        try (Connection conexion =
                     ConexionDB.getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("saldo_puntos");
                }
            }
        }

        return -1;
    }

    public Recompensa buscarRecompensaPorCodigo(
            String codigo
    ) throws SQLException {

        String sql = """
                SELECT
                    id_recompensa,
                    codigo,
                    nombre,
                    puntos_requeridos,
                    tipo_recompensa,
                    id_producto,
                    id_menu,
                    activo
                FROM recompensas
                WHERE codigo = ?
                """;

        Integer idProducto = null;
        Integer idMenu = null;

        int idRecompensa;
        String codigoRecompensa;
        String nombre;
        int puntosRequeridos;
        String tipoRecompensa;
        boolean activo;

        try (Connection conexion =
                     ConexionDB.getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                idRecompensa =
                        rs.getInt("id_recompensa");

                codigoRecompensa =
                        rs.getString("codigo");

                nombre =
                        rs.getString("nombre");

                puntosRequeridos =
                        rs.getInt("puntos_requeridos");

                tipoRecompensa =
                        rs.getString("tipo_recompensa");

                Object producto =
                        rs.getObject("id_producto");

                Object menu =
                        rs.getObject("id_menu");

                if (producto != null) {
                    idProducto =
                            ((Number) producto).intValue();
                }

                if (menu != null) {
                    idMenu =
                            ((Number) menu).intValue();
                }

                activo =
                        rs.getBoolean("activo");
            }
        }

        Producto producto = null;
        Menu menu = null;

        if (idProducto != null) {
            producto =
                    new ProductoDAO()
                            .buscarPorId(idProducto);
        }

        if (idMenu != null) {
            menu =
                    new MenuDAO()
                            .buscarPorId(idMenu);
        }

        return new Recompensa(
                idRecompensa,
                codigoRecompensa,
                nombre,
                puntosRequeridos,
                tipoRecompensa,
                producto,
                menu,
                activo
        );
    }

    public List<Recompensa> listarRecompensasActivas()
            throws SQLException {

        List<Recompensa> recompensas =
                new ArrayList<>();

        String sql = """
                SELECT codigo
                FROM recompensas
                WHERE activo = TRUE
                ORDER BY puntos_requeridos
                """;

        List<String> codigos =
                new ArrayList<>();

        try (Connection conexion =
                     ConexionDB.getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            while (rs.next()) {
                codigos.add(
                        rs.getString("codigo")
                );
            }
        }

        for (String codigo : codigos) {

            Recompensa recompensa =
                    buscarRecompensaPorCodigo(
                            codigo
                    );

            if (recompensa != null) {
                recompensas.add(recompensa);
            }
        }

        return recompensas;
    }

    public int registrarCanje(
            Connection conexion,
            int idCliente,
            int idRecompensa,
            int puntosRequeridos
    ) throws SQLException, PuntosInsuficientesException {

        String sqlCliente = """
                SELECT saldo_puntos, activo
                FROM clientes
                WHERE id_cliente = ?
                FOR UPDATE
                """;

        int saldoActual;
        boolean activo;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlCliente)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "El cliente no existe."
                    );
                }

                saldoActual =
                        rs.getInt("saldo_puntos");

                activo =
                        rs.getBoolean("activo");
            }
        }

        if (!activo) {
            throw new IllegalArgumentException(
                    "El cliente está desactivado."
            );
        }

        if (saldoActual < puntosRequeridos) {
            throw new PuntosInsuficientesException(
                    "Puntos insuficientes. Saldo disponible: "
                    + saldoActual
                    + "."
            );
        }

        String sqlCanje = """
                INSERT INTO canjes
                (
                    id_cliente,
                    id_recompensa,
                    puntos_utilizados
                )
                VALUES (?, ?, ?)
                """;

        int idCanje;

        try (PreparedStatement ps =
                     conexion.prepareStatement(
                             sqlCanje,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idRecompensa);
            ps.setInt(3, puntosRequeridos);

            ps.executeUpdate();

            try (ResultSet rs =
                         ps.getGeneratedKeys()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "No se pudo registrar el canje."
                    );
                }

                idCanje =
                        rs.getInt(1);
            }
        }

        int nuevoSaldo =
                saldoActual - puntosRequeridos;

        String sqlActualizar = """
                UPDATE clientes
                SET saldo_puntos = ?
                WHERE id_cliente = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlActualizar)) {

            ps.setInt(1, nuevoSaldo);
            ps.setInt(2, idCliente);

            ps.executeUpdate();
        }

        String sqlMovimiento = """
                INSERT INTO movimientos_puntos
                (
                    id_cliente,
                    tipo,
                    puntos,
                    saldo_resultante,
                    id_canje
                )
                VALUES (?, 'CANJE', ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlMovimiento)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, puntosRequeridos);
            ps.setInt(3, nuevoSaldo);
            ps.setInt(4, idCanje);

            ps.executeUpdate();
        }

        return idCanje;
    }

    public List<MovimientoPuntos> listarMovimientosPorCliente(
            int idCliente
    ) throws SQLException {

        List<MovimientoPuntos> movimientos =
                new ArrayList<>();

        Cliente cliente =
                new ClienteDAO()
                        .buscarPorId(idCliente);

        if (cliente == null) {
            return movimientos;
        }

        String sql = """
                SELECT
                    id_movimiento,
                    fecha,
                    tipo,
                    puntos,
                    saldo_resultante,
                    id_pedido,
                    id_canje
                FROM movimientos_puntos
                WHERE id_cliente = ?
                ORDER BY fecha DESC, id_movimiento DESC
                """;

        try (Connection conexion =
                     ConexionDB.getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    Integer idPedido =
                            obtenerEnteroNullable(
                                    rs,
                                    "id_pedido"
                            );

                    Integer idCanje =
                            obtenerEnteroNullable(
                                    rs,
                                    "id_canje"
                            );

                    movimientos.add(
                            new MovimientoPuntos(
                                    rs.getInt("id_movimiento"),
                                    cliente,
                                    rs.getTimestamp("fecha")
                                            .toLocalDateTime(),
                                    rs.getString("tipo"),
                                    rs.getInt("puntos"),
                                    rs.getInt("saldo_resultante"),
                                    idPedido,
                                    idCanje
                            )
                    );
                }
            }
        }

        return movimientos;
    }

    public List<Canje> listarCanjesPorCliente(
            int idCliente
    ) throws SQLException {

        List<Canje> canjes =
                new ArrayList<>();

        Cliente cliente =
                new ClienteDAO()
                        .buscarPorId(idCliente);

        if (cliente == null) {
            return canjes;
        }

        String sql = """
                SELECT
                    c.id_canje,
                    c.fecha,
                    c.puntos_utilizados,
                    r.id_recompensa,
                    r.codigo,
                    r.nombre,
                    r.puntos_requeridos,
                    r.tipo_recompensa,
                    r.activo
                FROM canjes c
                INNER JOIN recompensas r
                    ON c.id_recompensa = r.id_recompensa
                WHERE c.id_cliente = ?
                ORDER BY c.fecha DESC, c.id_canje DESC
                """;

        try (Connection conexion =
                     ConexionDB.getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    Recompensa recompensa =
                            new Recompensa(
                                    rs.getInt("id_recompensa"),
                                    rs.getString("codigo"),
                                    rs.getString("nombre"),
                                    rs.getInt("puntos_requeridos"),
                                    rs.getString("tipo_recompensa"),
                                    null,
                                    null,
                                    rs.getBoolean("activo")
                            );

                    canjes.add(
                            new Canje(
                                    rs.getInt("id_canje"),
                                    cliente,
                                    recompensa,
                                    rs.getTimestamp("fecha")
                                            .toLocalDateTime(),
                                    rs.getInt("puntos_utilizados")
                            )
                    );
                }
            }
        }

        return canjes;
    }

    private int obtenerSaldo(
            Connection conexion,
            int idCliente
    ) throws SQLException {

        String sql = """
                SELECT saldo_puntos
                FROM clientes
                WHERE id_cliente = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("saldo_puntos");
                }
            }
        }

        throw new SQLException(
                "No se encontró el cliente."
        );
    }

    private Integer obtenerEnteroNullable(
            ResultSet rs,
            String columna
    ) throws SQLException {

        Object valor =
                rs.getObject(columna);

        if (valor == null) {
            return null;
        }

        return ((Number) valor).intValue();
    }
}