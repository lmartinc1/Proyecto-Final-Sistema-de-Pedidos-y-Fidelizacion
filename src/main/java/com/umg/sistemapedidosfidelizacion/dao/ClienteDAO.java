package com.umg.sistemapedidosfidelizacion.dao;

import com.umg.sistemapedidosfidelizacion.config.ConexionDB;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean insertar(Cliente cliente) throws SQLException {
        String sql = """
                INSERT INTO clientes
                (codigo, nombre, telefono, saldo_puntos, activo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getCodigo());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getTelefono());
            ps.setInt(4, cliente.getSaldoPuntos());
            ps.setBoolean(5, cliente.isActivo());

            return ps.executeUpdate() > 0;
        }
    }

    public Cliente buscarPorCodigo(String codigo) throws SQLException {
        String sql = """
                SELECT id_cliente, codigo, nombre, telefono, saldo_puntos, activo
                FROM clientes
                WHERE codigo = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        }

        return null;
    }

    public Cliente buscarPorId(int idCliente) throws SQLException {
        String sql = """
                SELECT id_cliente, codigo, nombre, telefono, saldo_puntos, activo
                FROM clientes
                WHERE id_cliente = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        }

        return null;
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();

        String sql = """
                SELECT id_cliente, codigo, nombre, telefono, saldo_puntos, activo
                FROM clientes
                ORDER BY id_cliente
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        }

        return clientes;
    }

    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = """
                UPDATE clientes
                SET nombre = ?, telefono = ?, activo = ?
                WHERE id_cliente = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setBoolean(3, cliente.isActivo());
            ps.setInt(4, cliente.getIdCliente());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean desactivar(int idCliente) throws SQLException {
        String sql = """
                UPDATE clientes
                SET activo = FALSE
                WHERE id_cliente = ?
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeCodigo(String codigo) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS cantidad
                FROM clientes
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

    public String generarSiguienteCodigo() throws SQLException {
        String sql = """
                SELECT COALESCE(MAX(id_cliente), 0) + 1 AS siguiente
                FROM clientes
                """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return String.format("CLI%03d", rs.getInt("siguiente"));
            }
        }

        return "CLI001";
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getInt("saldo_puntos"),
                rs.getBoolean("activo")
        );
    }
}