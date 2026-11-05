package com.umg.sistemapedidosfidelizacion.dao;

import com.umg.sistemapedidosfidelizacion.config.ConexionDB;
import com.umg.sistemapedidosfidelizacion.modelo.Menu;
import com.umg.sistemapedidosfidelizacion.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    private static final String SELECT_BASE = """
            SELECT
                m.id_menu AS menu_id,
                m.codigo AS menu_codigo,
                m.nombre AS menu_nombre,
                m.precio AS menu_precio,
                m.activo AS menu_activo,

                s.id_producto AS s_id,
                s.codigo AS s_codigo,
                s.categoria AS s_categoria,
                s.nombre AS s_nombre,
                s.precio AS s_precio,
                s.existencia AS s_existencia,
                s.activo AS s_activo,

                b.id_producto AS b_id,
                b.codigo AS b_codigo,
                b.categoria AS b_categoria,
                b.nombre AS b_nombre,
                b.precio AS b_precio,
                b.existencia AS b_existencia,
                b.activo AS b_activo,

                a.id_producto AS a_id,
                a.codigo AS a_codigo,
                a.categoria AS a_categoria,
                a.nombre AS a_nombre,
                a.precio AS a_precio,
                a.existencia AS a_existencia,
                a.activo AS a_activo

            FROM menus m
            INNER JOIN productos s
                ON m.id_sandwich = s.id_producto
            INNER JOIN productos b
                ON m.id_bebida = b.id_producto
            INNER JOIN productos a
                ON m.id_acompanamiento = a.id_producto
            """;

    public boolean insertar(Menu menu) throws SQLException {
        String sql = """
                INSERT INTO menus
                (
                    codigo,
                    nombre,
                    id_sandwich,
                    id_bebida,
                    id_acompanamiento,
                    precio,
                    activo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, menu.getCodigo());
            ps.setString(2, menu.getNombre());
            ps.setInt(3, menu.getSandwich().getIdProducto());
            ps.setInt(4, menu.getBebida().getIdProducto());
            ps.setInt(5, menu.getAcompanamiento().getIdProducto());
            ps.setBigDecimal(6, menu.getPrecio());
            ps.setBoolean(7, menu.isActivo());

            return ps.executeUpdate() > 0;
        }
    }

    public Menu buscarPorId(int idMenu) throws SQLException {
        String sql = SELECT_BASE + """
                WHERE m.id_menu = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idMenu);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMenu(rs);
                }
            }
        }

        return null;
    }

    public Menu buscarPorCodigo(String codigo) throws SQLException {
        String sql = SELECT_BASE + """
                WHERE m.codigo = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMenu(rs);
                }
            }
        }

        return null;
    }

    public List<Menu> listar() throws SQLException {
        List<Menu> menus = new ArrayList<>();

        String sql = SELECT_BASE + """
                ORDER BY m.id_menu
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                menus.add(mapearMenu(rs));
            }
        }

        return menus;
    }

    public List<Menu> listarActivos() throws SQLException {
        List<Menu> menus = new ArrayList<>();

        String sql = SELECT_BASE + """
                WHERE m.activo = TRUE
                ORDER BY m.nombre
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                menus.add(mapearMenu(rs));
            }
        }

        return menus;
    }

    public boolean actualizar(Menu menu) throws SQLException {
        String sql = """
                UPDATE menus
                SET nombre = ?,
                    id_sandwich = ?,
                    id_bebida = ?,
                    id_acompanamiento = ?,
                    precio = ?,
                    activo = ?
                WHERE id_menu = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, menu.getNombre());
            ps.setInt(2, menu.getSandwich().getIdProducto());
            ps.setInt(3, menu.getBebida().getIdProducto());
            ps.setInt(4, menu.getAcompanamiento().getIdProducto());
            ps.setBigDecimal(5, menu.getPrecio());
            ps.setBoolean(6, menu.isActivo());
            ps.setInt(7, menu.getIdMenu());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean desactivar(int idMenu) throws SQLException {
        String sql = """
                UPDATE menus
                SET activo = FALSE
                WHERE id_menu = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idMenu);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeCodigo(String codigo) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS cantidad
                FROM menus
                WHERE codigo = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("cantidad") > 0;
            }
        }
    }

    private Menu mapearMenu(ResultSet rs) throws SQLException {

        Producto sandwich = mapearProducto(rs, "s");
        Producto bebida = mapearProducto(rs, "b");
        Producto acompanamiento = mapearProducto(rs, "a");

        return new Menu(
                rs.getInt("menu_id"),
                rs.getString("menu_codigo"),
                rs.getString("menu_nombre"),
                sandwich,
                bebida,
                acompanamiento,
                rs.getBigDecimal("menu_precio"),
                rs.getBoolean("menu_activo")
        );
    }

    private Producto mapearProducto(ResultSet rs, String prefijo)
            throws SQLException {

        return new Producto(
                rs.getInt(prefijo + "_id"),
                rs.getString(prefijo + "_codigo"),
                rs.getString(prefijo + "_categoria"),
                rs.getString(prefijo + "_nombre"),
                rs.getBigDecimal(prefijo + "_precio"),
                rs.getInt(prefijo + "_existencia"),
                rs.getBoolean(prefijo + "_activo")
        );
    }
}