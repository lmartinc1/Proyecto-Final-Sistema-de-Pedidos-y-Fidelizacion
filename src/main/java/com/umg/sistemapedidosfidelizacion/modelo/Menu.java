package com.umg.sistemapedidosfidelizacion.modelo;

import java.math.BigDecimal;

public class Menu {

    private int idMenu;
    private String codigo;
    private String nombre;
    private Producto sandwich;
    private Producto bebida;
    private Producto acompanamiento;
    private BigDecimal precio;
    private boolean activo;

    public Menu() {
    }

    public Menu(int idMenu, String codigo, String nombre, Producto sandwich,
                Producto bebida, Producto acompanamiento, BigDecimal precio,
                boolean activo) {
        this.idMenu = idMenu;
        this.codigo = codigo;
        this.nombre = nombre;
        this.sandwich = sandwich;
        this.bebida = bebida;
        this.acompanamiento = acompanamiento;
        this.precio = precio;
        this.activo = activo;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Producto getSandwich() {
        return sandwich;
    }

    public void setSandwich(Producto sandwich) {
        this.sandwich = sandwich;
    }

    public Producto getBebida() {
        return bebida;
    }

    public void setBebida(Producto bebida) {
        this.bebida = bebida;
    }

    public Producto getAcompanamiento() {
        return acompanamiento;
    }

    public void setAcompanamiento(Producto acompanamiento) {
        this.acompanamiento = acompanamiento;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}