package com.umg.sistemapedidosfidelizacion.dao;

import com.umg.sistemapedidosfidelizacion.config.ConexionDB;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;
import com.umg.sistemapedidosfidelizacion.modelo.DetallePedido;
import com.umg.sistemapedidosfidelizacion.modelo.Menu;
import com.umg.sistemapedidosfidelizacion.modelo.Pedido;
import com.umg.sistemapedidosfidelizacion.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public int insertar(Pedido pedido) throws SQLException {
        String sqlPedido = """
                INSERT INTO pedidos
                (id_cliente, fecha, estado, total)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.getConnection()) {

            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try {
                int idPedido;

                try (PreparedStatement ps = conexion.prepareStatement(
                        sqlPedido,
                        Statement.RETURN_GENERATED_KEYS
                )) {

                    ps.setInt(1, pedido.getCliente().getIdCliente());
                    ps.setTimestamp(2, Timestamp.valueOf(pedido.getFecha()));
                    ps.setString(3, pedido.getEstado());
                    ps.setBigDecimal(4, pedido.getTotal());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID del pedido."
                            );
                        }

                        idPedido = rs.getInt(1);
                    }
                }

                insertarDetalles(
                        conexion,
                        idPedido,
                        pedido.getDetalles()
                );

                conexion.commit();

                pedido.setIdPedido(idPedido);

                return idPedido;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;

            } finally {
                conexion.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    private void insertarDetalles(
            Connection conexion,
            int idPedido,
            List<DetallePedido> detalles
    ) throws SQLException {

        String sql = """
                INSERT INTO detalle_pedido
                (
                    id_pedido,
                    tipo_item,
                    id_producto,
                    id_menu,
                    cantidad,
                    precio_unitario,
                    subtotal
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            for (DetallePedido detalle : detalles) {

                ps.setInt(1, idPedido);
                ps.setString(2, detalle.getTipoItem());

                if ("PRODUCTO".equalsIgnoreCase(detalle.getTipoItem())) {
                    ps.setInt(
                            3,
                            detalle.getProducto().getIdProducto()
                    );
                    ps.setNull(4, Types.INTEGER);

                } else {
                    ps.setNull(3, Types.INTEGER);
                    ps.setInt(
                            4,
                            detalle.getMenu().getIdMenu()
                    );
                }

                ps.setInt(5, detalle.getCantidad());
                ps.setBigDecimal(6, detalle.getPrecioUnitario());
                ps.setBigDecimal(7, detalle.getSubtotal());

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    public Pedido buscarPorId(int idPedido) throws SQLException {
        String sql = """
                SELECT
                    p.id_pedido,
                    p.fecha,
                    p.estado,
                    p.total,

                    c.id_cliente,
                    c.codigo,
                    c.nombre,
                    c.telefono,
                    c.saldo_puntos,
                    c.activo

                FROM pedidos p
                INNER JOIN clientes c
                    ON p.id_cliente = c.id_cliente

                WHERE p.id_pedido = ?
                """;

        Pedido pedido = null;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Cliente cliente = new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getInt("saldo_puntos"),
                            rs.getBoolean("activo")
                    );

                    pedido = new Pedido(
                            rs.getInt("id_pedido"),
                            cliente,
                            rs.getTimestamp("fecha").toLocalDateTime(),
                            rs.getString("estado"),
                            rs.getBigDecimal("total")
                    );
                }
            }
        }

        if (pedido != null) {
            pedido.setDetalles(
                    listarDetalles(idPedido)
            );
        }

        return pedido;
    }

    public List<Pedido> listar() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = """
                SELECT
                    p.id_pedido,
                    p.fecha,
                    p.estado,
                    p.total,

                    c.id_cliente,
                    c.codigo,
                    c.nombre,
                    c.telefono,
                    c.saldo_puntos,
                    c.activo

                FROM pedidos p
                INNER JOIN clientes c
                    ON p.id_cliente = c.id_cliente

                ORDER BY p.id_pedido DESC
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cliente cliente = new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getInt("saldo_puntos"),
                        rs.getBoolean("activo")
                );

                Pedido pedido = new Pedido(
                        rs.getInt("id_pedido"),
                        cliente,
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getString("estado"),
                        rs.getBigDecimal("total")
                );

                pedidos.add(pedido);
            }
        }

        return pedidos;
    }


    public List<Pedido> listarPorCliente(int idCliente) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = """
                SELECT
                    p.id_pedido,
                    p.fecha,
                    p.estado,
                    p.total,

                    c.id_cliente,
                    c.codigo,
                    c.nombre,
                    c.telefono,
                    c.saldo_puntos,
                    c.activo

                FROM pedidos p
                INNER JOIN clientes c
                    ON p.id_cliente = c.id_cliente

                WHERE p.id_cliente = ?
                ORDER BY p.fecha DESC, p.id_pedido DESC
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Cliente cliente = new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getInt("saldo_puntos"),
                            rs.getBoolean("activo")
                    );

                    pedidos.add(
                            new Pedido(
                                    rs.getInt("id_pedido"),
                                    cliente,
                                    rs.getTimestamp("fecha").toLocalDateTime(),
                                    rs.getString("estado"),
                                    rs.getBigDecimal("total")
                            )
                    );
                }
            }
        }

        return pedidos;
    }

    private List<DetallePedido> listarDetalles(int idPedido)
            throws SQLException {

        List<DetallePedido> detalles = new ArrayList<>();

        ProductoDAO productoDAO = new ProductoDAO();
        MenuDAO menuDAO = new MenuDAO();

        String sql = """
                SELECT
                    id_detalle,
                    tipo_item,
                    id_producto,
                    id_menu,
                    cantidad,
                    precio_unitario,
                    subtotal

                FROM detalle_pedido
                WHERE id_pedido = ?
                ORDER BY id_detalle
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String tipoItem =
                            rs.getString("tipo_item");

                    Producto producto = null;
                    Menu menu = null;

                    if ("PRODUCTO".equalsIgnoreCase(tipoItem)) {

                        producto = productoDAO.buscarPorId(
                                rs.getInt("id_producto")
                        );

                    } else if ("MENU".equalsIgnoreCase(tipoItem)) {

                        menu = menuDAO.buscarPorId(
                                rs.getInt("id_menu")
                        );
                    }

                    DetallePedido detalle =
                            new DetallePedido(
                                    rs.getInt("id_detalle"),
                                    tipoItem,
                                    producto,
                                    menu,
                                    rs.getInt("cantidad"),
                                    rs.getBigDecimal("precio_unitario"),
                                    rs.getBigDecimal("subtotal")
                            );

                    detalles.add(detalle);
                }
            }
        }

        return detalles;
    }

    public String obtenerEstadoParaActualizar(
            Connection conexion,
            int idPedido
    ) throws SQLException {

        String sql = """
                SELECT estado
                FROM pedidos
                WHERE id_pedido = ?
                FOR UPDATE
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("estado");
                }
            }
        }

        return null;
    }

    public boolean actualizarEstado(
            int idPedido,
            String estado
    ) throws SQLException {

        try (Connection conexion = ConexionDB.getConnection()) {
            return actualizarEstado(
                    conexion,
                    idPedido,
                    estado
            );
        }
    }

    public boolean actualizarEstado(
            Connection conexion,
            int idPedido,
            String estado
    ) throws SQLException {

        String sql = """
                UPDATE pedidos
                SET estado = ?
                WHERE id_pedido = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, idPedido);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean anularPendiente(int idPedido)
            throws SQLException {

        String sql = """
                UPDATE pedidos
                SET estado = 'ANULADO'
                WHERE id_pedido = ?
                  AND estado = 'PENDIENTE'
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            return ps.executeUpdate() > 0;
        }
    }
}