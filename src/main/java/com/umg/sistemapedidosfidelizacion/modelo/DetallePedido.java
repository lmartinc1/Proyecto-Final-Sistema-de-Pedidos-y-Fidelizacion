package com.umg.sistemapedidosfidelizacion.modelo;

import java.math.BigDecimal;

public class DetallePedido {

    private int idDetalle;
    private String tipoItem;
    private Producto producto;
    private Menu menu;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetallePedido() {
    }

    public DetallePedido(int idDetalle, String tipoItem, Producto producto,
                         Menu menu, int cantidad, BigDecimal precioUnitario,
                         BigDecimal subtotal) {
        this.idDetalle = idDetalle;
        this.tipoItem = tipoItem;
        this.producto = producto;
        this.menu = menu;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public DetallePedido(String tipoItem, Producto producto, Menu menu,
                         int cantidad, BigDecimal precioUnitario) {
        this.tipoItem = tipoItem;
        this.producto = producto;
        this.menu = menu;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        recalcularSubtotal();
    }

    public void recalcularSubtotal() {
        if (precioUnitario != null && cantidad > 0) {
            subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        } else {
            subtotal = BigDecimal.ZERO;
        }
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(String tipoItem) {
        this.tipoItem = tipoItem;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        recalcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        recalcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}