package com.umg.sistemapedidosfidelizacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int idPedido;
    private Cliente cliente;
    private LocalDateTime fecha;
    private String estado;
    private BigDecimal total;
    private List<DetallePedido> detalles;

    public Pedido() {
        this.detalles = new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }

    public Pedido(int idPedido, Cliente cliente, LocalDateTime fecha,
                  String estado, BigDecimal total) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.detalles = new ArrayList<>();
    }

    public void agregarDetalle(DetallePedido detalle) {
        if (detalle != null) {
            detalles.add(detalle);
            calcularTotal();
        }
    }

    public void calcularTotal() {
        total = BigDecimal.ZERO;

        for (DetallePedido detalle : detalles) {
            if (detalle.getSubtotal() != null) {
                total = total.add(detalle.getSubtotal());
            }
        }
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles != null ? detalles : new ArrayList<>();
        calcularTotal();
    }
}