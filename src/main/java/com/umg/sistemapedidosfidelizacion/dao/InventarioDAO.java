package com.umg.sistemapedidosfidelizacion.dao;

import com.umg.sistemapedidosfidelizacion.config.ConexionDB;
import com.umg.sistemapedidosfidelizacion.excepcion.InventarioInsuficienteException;
import com.umg.sistemapedidosfidelizacion.modelo.MovimientoInventario;
import com.umg.sistemapedidosfidelizacion.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    public boolean abastecer(int idProducto, int cantidad, String referencia)
            throws SQLException {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        String sqlActualizar = """
                UPDATE productos
                SET existencia = existencia + ?
                WHERE id_producto = ?
                """;

        try (Connection conexion = ConexionDB.getConnection()) {

            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try {
                int filas;

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlActualizar)) {

                    ps.setInt(1, cantidad);
                    ps.setInt(2, idProducto);

                    filas = ps.executeUpdate();
                }

                if (filas == 0) {
                    conexion.rollback();
                    return false;
                }

                registrarMovimiento(
                        conexion,
                        idProducto,
                        "ABASTECIMIENTO",
                        cantidad,
                        referencia
                );

                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;

            } finally {
                conexion.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    public int obtenerExistencia(int idProducto) throws SQLException {
        String sql = """
                SELECT existencia
                FROM productos
                WHERE id_producto = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("existencia");
                }
            }
        }

        return -1;
    }

    public List<MovimientoInventario> listarMovimientos()
            throws SQLException {

        List<MovimientoInventario> movimientos = new ArrayList<>();

        String sql = """
                SELECT
                    mi.id_movimiento,
                    mi.fecha,
                    mi.tipo,
                    mi.cantidad,
                    mi.referencia,

                    p.id_producto,
                    p.codigo,
                    p.categoria,
                    p.nombre,
                    p.precio,
                    p.existencia,
                    p.activo

                FROM movimientos_inventario mi
                INNER JOIN productos p
                    ON mi.id_producto = p.id_producto

                ORDER BY mi.fecha DESC, mi.id_movimiento DESC
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Producto producto = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("codigo"),
                        rs.getString("categoria"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("existencia"),
                        rs.getBoolean("activo")
                );

                movimientos.add(
                        new MovimientoInventario(
                                rs.getInt("id_movimiento"),
                                producto,
                                rs.getTimestamp("fecha").toLocalDateTime(),
                                rs.getString("tipo"),
                                rs.getInt("cantidad"),
                                rs.getString("referencia")
                        )
                );
            }
        }

        return movimientos;
    }

    public void descontar(
            Connection conexion,
            int idProducto,
            int cantidad,
            String tipo,
            String referencia
    ) throws SQLException, InventarioInsuficienteException {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        String sqlConsultar = """
                SELECT nombre, existencia
                FROM productos
                WHERE id_producto = ?
                  AND activo = TRUE
                FOR UPDATE
                """;

        String nombreProducto;
        int existencia;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlConsultar)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new InventarioInsuficienteException(
                            "El producto no existe o está desactivado."
                    );
                }

                nombreProducto = rs.getString("nombre");
                existencia = rs.getInt("existencia");
            }
        }

        if (existencia < cantidad) {
            throw new InventarioInsuficienteException(
                    "Inventario insuficiente para "
                    + nombreProducto
                    + ". Disponible: "
                    + existencia
                    + "."
            );
        }

        String sqlActualizar = """
                UPDATE productos
                SET existencia = existencia - ?
                WHERE id_producto = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlActualizar)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);

            ps.executeUpdate();
        }

        registrarMovimiento(
                conexion,
                idProducto,
                tipo,
                cantidad,
                referencia
        );
    }

    private void registrarMovimiento(
            Connection conexion,
            int idProducto,
            String tipo,
            int cantidad,
            String referencia
    ) throws SQLException {

        String sql = """
                INSERT INTO movimientos_inventario
                (id_producto, tipo, cantidad, referencia)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ps.setString(2, tipo);
            ps.setInt(3, cantidad);
            ps.setString(4, referencia);

            ps.executeUpdate();
        }
    }
}