package com.umg.sistemapedidosfidelizacion.dao;

import com.umg.sistemapedidosfidelizacion.config.ConexionDB;
import com.umg.sistemapedidosfidelizacion.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean insertar(Producto producto) throws SQLException {
        String sql = """
                INSERT INTO productos
                (codigo, categoria, nombre, precio, existencia, activo)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getCategoria());
            ps.setString(3, producto.getNombre());
            ps.setBigDecimal(4, producto.getPrecio());
            ps.setInt(5, producto.getExistencia());
            ps.setBoolean(6, producto.isActivo());

            return ps.executeUpdate() > 0;
        }
    }

    public Producto buscarPorCodigo(String codigo) throws SQLException {
        String sql = """
                SELECT id_producto, codigo, categoria, nombre,
                       precio, existencia, activo
                FROM productos
                WHERE codigo = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }

        return null;
    }

    public Producto buscarPorId(int idProducto) throws SQLException {
        String sql = """
                SELECT id_producto, codigo, categoria, nombre,
                       precio, existencia, activo
                FROM productos
                WHERE id_producto = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }

        return null;
    }

    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT id_producto, codigo, categoria, nombre,
                       precio, existencia, activo
                FROM productos
                ORDER BY id_producto
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }

        return productos;
    }

    public List<Producto> listarActivos() throws SQLException {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT id_producto, codigo, categoria, nombre,
                       precio, existencia, activo
                FROM productos
                WHERE activo = TRUE
                ORDER BY categoria, nombre
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }

        return productos;
    }

    public List<Producto> listarPorCategoria(String categoria) throws SQLException {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT id_producto, codigo, categoria, nombre,
                       precio, existencia, activo
                FROM productos
                WHERE categoria = ?
                  AND activo = TRUE
                ORDER BY nombre
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, categoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        }

        return productos;
    }

    public boolean actualizar(Producto producto) throws SQLException {
        String sql = """
                UPDATE productos
                SET categoria = ?,
                    nombre = ?,
                    precio = ?,
                    activo = ?
                WHERE id_producto = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, producto.getCategoria());
            ps.setString(2, producto.getNombre());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setBoolean(4, producto.isActivo());
            ps.setInt(5, producto.getIdProducto());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean desactivar(int idProducto) throws SQLException {
        String sql = """
                UPDATE productos
                SET activo = FALSE
                WHERE id_producto = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeCodigo(String codigo) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS cantidad
                FROM productos
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

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("codigo"),
                rs.getString("categoria"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio"),
                rs.getInt("existencia"),
                rs.getBoolean("activo")
        );
    }
}