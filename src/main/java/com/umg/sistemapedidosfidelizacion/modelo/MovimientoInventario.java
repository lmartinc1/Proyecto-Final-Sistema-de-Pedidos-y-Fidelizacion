package com.umg.sistemapedidosfidelizacion.modelo;

import java.time.LocalDateTime;

public class MovimientoInventario {

    private int idMovimiento;
    private Producto producto;
    private LocalDateTime fecha;
    private String tipo;
    private int cantidad;
    private String referencia;

    public MovimientoInventario() {
    }

    public MovimientoInventario(int idMovimiento, Producto producto,
                                LocalDateTime fecha, String tipo,
                                int cantidad, String referencia) {
        this.idMovimiento = idMovimiento;
        this.producto = producto;
        this.fecha = fecha;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.referencia = referencia;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}