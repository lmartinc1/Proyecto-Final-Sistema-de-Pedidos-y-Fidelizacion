package com.umg.sistemapedidosfidelizacion.dao;

import com.umg.sistemapedidosfidelizacion.config.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<String[]> ventasPorPeriodo(
            LocalDate inicio,
            LocalDate fin
    ) throws SQLException {

        List<String[]> filas = new ArrayList<>();

        String sql = """
                SELECT
                    pa.fecha,
                    pa.metodo,
                    pa.monto,
                    p.id_pedido,
                    c.codigo,
                    c.nombre
                FROM pagos pa
                INNER JOIN pedidos p
                    ON pa.id_pedido = p.id_pedido
                INNER JOIN clientes c
                    ON p.id_cliente = c.id_cliente
                WHERE pa.estado = 'APROBADO'
                  AND pa.fecha >= ?
                  AND pa.fecha < ?
                ORDER BY pa.fecha
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setTimestamp(
                    1,
                    Timestamp.valueOf(inicio.atStartOfDay())
            );

            ps.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            fin.plusDays(1).atStartOfDay()
                    )
            );

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    filas.add(
                            new String[]{
                                    rs.getTimestamp("fecha")
                                            .toLocalDateTime()
                                            .format(FECHA_HORA),

                                    String.valueOf(
                                            rs.getInt("id_pedido")
                                    ),

                                    rs.getString("codigo"),

                                    rs.getString("nombre"),

                                    rs.getString("metodo"),

                                    "Q" + rs.getBigDecimal("monto")
                            }
                    );
                }
            }
        }

        return filas;
    }

    public List<String[]> productosMasVendidos()
            throws SQLException {

        List<String[]> filas = new ArrayList<>();

        String sql = """
                SELECT
                    pr.codigo,
                    pr.nombre,
                    pr.categoria,
                    SUM(x.cantidad) AS cantidad_vendida
                FROM (
                    SELECT
                        dp.id_producto AS id_producto,
                        dp.cantidad AS cantidad
                    FROM detalle_pedido dp
                    INNER JOIN pedidos p
                        ON dp.id_pedido = p.id_pedido
                    WHERE dp.tipo_item = 'PRODUCTO'
                      AND p.estado IN ('PAGADO', 'ENTREGADO')

                    UNION ALL

                    SELECT
                        m.id_sandwich,
                        dp.cantidad
                    FROM detalle_pedido dp
                    INNER JOIN pedidos p
                        ON dp.id_pedido = p.id_pedido
                    INNER JOIN menus m
                        ON dp.id_menu = m.id_menu
                    WHERE dp.tipo_item = 'MENU'
                      AND p.estado IN ('PAGADO', 'ENTREGADO')

                    UNION ALL

                    SELECT
                        m.id_bebida,
                        dp.cantidad
                    FROM detalle_pedido dp
                    INNER JOIN pedidos p
                        ON dp.id_pedido = p.id_pedido
                    INNER JOIN menus m
                        ON dp.id_menu = m.id_menu
                    WHERE dp.tipo_item = 'MENU'
                      AND p.estado IN ('PAGADO', 'ENTREGADO')

                    UNION ALL

                    SELECT
                        m.id_acompanamiento,
                        dp.cantidad
                    FROM detalle_pedido dp
                    INNER JOIN pedidos p
                        ON dp.id_pedido = p.id_pedido
                    INNER JOIN menus m
                        ON dp.id_menu = m.id_menu
                    WHERE dp.tipo_item = 'MENU'
                      AND p.estado IN ('PAGADO', 'ENTREGADO')
                ) x
                INNER JOIN productos pr
                    ON x.id_producto = pr.id_producto
                GROUP BY
                    pr.id_producto,
                    pr.codigo,
                    pr.nombre,
                    pr.categoria
                ORDER BY cantidad_vendida DESC, pr.nombre
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                filas.add(
                        new String[]{
                                rs.getString("codigo"),
                                rs.getString("nombre"),
                                rs.getString("categoria"),
                                String.valueOf(
                                        rs.getInt("cantidad_vendida")
                                )
                        }
                );
            }
        }

        return filas;
    }

    public List<String[]> productosBajoInventario()
            throws SQLException {

        List<String[]> filas = new ArrayList<>();

        String sql = """
                SELECT
                    codigo,
                    categoria,
                    nombre,
                    existencia,
                    activo
                FROM productos
                WHERE existencia <= 5
                ORDER BY existencia, nombre
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                filas.add(
                        new String[]{
                                rs.getString("codigo"),
                                rs.getString("categoria"),
                                rs.getString("nombre"),
                                String.valueOf(
                                        rs.getInt("existencia")
                                ),
                                rs.getBoolean("activo")
                                        ? "Activo"
                                        : "Inactivo"
                        }
                );
            }
        }

        return filas;
    }

    public List<String[]> puntosPorCliente()
            throws SQLException {

        List<String[]> filas = new ArrayList<>();

        String sql = """
                SELECT
                    codigo,
                    nombre,
                    telefono,
                    saldo_puntos,
                    activo
                FROM clientes
                ORDER BY saldo_puntos DESC, nombre
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                filas.add(
                        new String[]{
                                rs.getString("codigo"),
                                rs.getString("nombre"),
                                rs.getString("telefono"),
                                String.valueOf(
                                        rs.getInt("saldo_puntos")
                                ),
                                rs.getBoolean("activo")
                                        ? "Activo"
                                        : "Inactivo"
                        }
                );
            }
        }

        return filas;
    }

    public List<String[]> historialCanjes()
            throws SQLException {

        List<String[]> filas = new ArrayList<>();

        String sql = """
                SELECT
                    ca.fecha,
                    c.codigo,
                    c.nombre AS cliente,
                    r.nombre AS recompensa,
                    ca.puntos_utilizados
                FROM canjes ca
                INNER JOIN clientes c
                    ON ca.id_cliente = c.id_cliente
                INNER JOIN recompensas r
                    ON ca.id_recompensa = r.id_recompensa
                ORDER BY ca.fecha DESC, ca.id_canje DESC
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                filas.add(
                        new String[]{
                                rs.getTimestamp("fecha")
                                    .toLocalDateTime()
                                    .format(FECHA_HORA),

                                rs.getString("codigo"),

                                rs.getString("cliente"),

                                rs.getString("recompensa"),

                                String.valueOf(
                                        rs.getInt("puntos_utilizados")
                                )
                        }
                );
            }
        }

        return filas;
    }

    public List<String[]> ventasMenus()
            throws SQLException {

        List<String[]> filas = new ArrayList<>();

        String sql = """
                SELECT
                    p.id_pedido,
                    p.fecha,
                    c.codigo AS codigo_cliente,
                    c.nombre AS cliente,
                    m.codigo AS codigo_menu,
                    m.nombre AS menu,
                    dp.cantidad,
                    dp.precio_unitario,
                    dp.subtotal
                FROM detalle_pedido dp
                INNER JOIN pedidos p
                    ON dp.id_pedido = p.id_pedido
                INNER JOIN clientes c
                    ON p.id_cliente = c.id_cliente
                INNER JOIN menus m
                    ON dp.id_menu = m.id_menu
                WHERE dp.tipo_item = 'MENU'
                  AND p.estado IN ('PAGADO', 'ENTREGADO')
                ORDER BY p.fecha DESC, p.id_pedido DESC
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                filas.add(
                        new String[]{
                                String.valueOf(
                                        rs.getInt("id_pedido")
                                ),

                                rs.getTimestamp("fecha")
                                    .toLocalDateTime()
                                    .format(FECHA_HORA),

                                rs.getString("codigo_cliente"),

                                rs.getString("cliente"),

                                rs.getString("codigo_menu"),

                                rs.getString("menu"),

                                String.valueOf(
                                        rs.getInt("cantidad")
                                ),

                                "Q" + rs.getBigDecimal(
                                        "precio_unitario"
                                ),

                                "Q" + rs.getBigDecimal(
                                        "subtotal"
                                )
                        }
                );
            }
        }

        return filas;
    }
}