package com.umg.sistemapedidosfidelizacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class MetodoPago {

    private int idPago;
    private Pedido pedido;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private String estado;
    private String referencia;

    public MetodoPago() {
    }

    public MetodoPago(int idPago, Pedido pedido, LocalDateTime fecha,
                      BigDecimal monto, String estado, String referencia) {
        this.idPago = idPago;
        this.pedido = pedido;
        this.fecha = fecha;
        this.monto = monto;
        this.estado = estado;
        this.referencia = referencia;
    }

    public abstract boolean validarPago();

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}