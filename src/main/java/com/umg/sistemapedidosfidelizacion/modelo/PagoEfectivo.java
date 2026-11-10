package com.umg.sistemapedidosfidelizacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoEfectivo extends MetodoPago {

    private BigDecimal montoRecibido;
    private BigDecimal cambio;

    public PagoEfectivo() {
    }

    public PagoEfectivo(int idPago, Pedido pedido, LocalDateTime fecha,
                        BigDecimal monto, String estado, BigDecimal montoRecibido,
                        BigDecimal cambio) {
        super(idPago, pedido, fecha, monto, estado, null);
        this.montoRecibido = montoRecibido;
        this.cambio = cambio;
    }

    @Override
    public boolean validarPago() {
        return getMonto() != null
                && montoRecibido != null
                && montoRecibido.compareTo(getMonto()) >= 0;
    }

    public BigDecimal calcularCambio() {
        if (!validarPago()) {
            cambio = BigDecimal.ZERO;
            return cambio;
        }

        cambio = montoRecibido.subtract(getMonto());
        return cambio;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }
}