package com.umg.sistemapedidosfidelizacion.dao;

import com.umg.sistemapedidosfidelizacion.modelo.MetodoPago;
import com.umg.sistemapedidosfidelizacion.modelo.PagoEfectivo;
import com.umg.sistemapedidosfidelizacion.modelo.PagoTarjeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

public class PagoDAO {

    public int insertar(
            Connection conexion,
            MetodoPago pago
    ) throws SQLException {

        String metodo;

        if (pago instanceof PagoEfectivo) {
            metodo = "EFECTIVO";
        } else if (pago instanceof PagoTarjeta) {
            metodo = "TARJETA";
        } else {
            throw new IllegalArgumentException(
                    "Método de pago no válido."
            );
        }

        String sql = """
                INSERT INTO pagos
                (
                    id_pedido,
                    fecha,
                    metodo,
                    monto,
                    monto_recibido,
                    cambio,
                    estado,
                    referencia
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            ps.setInt(
                    1,
                    pago.getPedido().getIdPedido()
            );

            ps.setTimestamp(
                    2,
                    Timestamp.valueOf(pago.getFecha())
            );

            ps.setString(3, metodo);
            ps.setBigDecimal(4, pago.getMonto());

            if (pago instanceof PagoEfectivo efectivo) {

                ps.setBigDecimal(
                        5,
                        efectivo.getMontoRecibido()
                );

                ps.setBigDecimal(
                        6,
                        efectivo.getCambio()
                );

            } else {
                ps.setNull(5, Types.DECIMAL);
                ps.setNull(6, Types.DECIMAL);
            }

            ps.setString(7, pago.getEstado());

            if (pago.getReferencia() == null
                    || pago.getReferencia().isBlank()) {

                ps.setNull(8, Types.VARCHAR);

            } else {
                ps.setString(
                        8,
                        pago.getReferencia()
                );
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "No se pudo obtener el ID del pago."
                    );
                }

                int idPago = rs.getInt(1);

                pago.setIdPago(idPago);

                return idPago;
            }
        }
    }
}