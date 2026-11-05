package com.umg.sistemapedidosfidelizacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoTarjeta extends MetodoPago {

    public PagoTarjeta() {
    }

    public PagoTarjeta(int idPago, Pedido pedido, LocalDateTime fecha,
                       BigDecimal monto, String estado, String referencia) {
        super(idPago, pedido, fecha, monto, estado, referencia);
    }

    @Override
    public boolean validarPago() {
        return "APROBADO".equalsIgnoreCase(getEstado())
                && getReferencia() != null
                && !getReferencia().isBlank();
    }
}