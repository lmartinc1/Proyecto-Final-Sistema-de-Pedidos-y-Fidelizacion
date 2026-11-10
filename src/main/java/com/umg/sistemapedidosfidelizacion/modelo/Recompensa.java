package com.umg.sistemapedidosfidelizacion.modelo;

public class Recompensa {

    private int idRecompensa;
    private String codigo;
    private String nombre;
    private int puntosRequeridos;
    private String tipoRecompensa;
    private Producto producto;
    private Menu menu;
    private boolean activo;

    public Recompensa() {
    }

    public Recompensa(int idRecompensa, String codigo, String nombre,
                      int puntosRequeridos, String tipoRecompensa,
                      Producto producto, Menu menu, boolean activo) {
        this.idRecompensa = idRecompensa;
        this.codigo = codigo;
        this.nombre = nombre;
        this.puntosRequeridos = puntosRequeridos;
        this.tipoRecompensa = tipoRecompensa;
        this.producto = producto;
        this.menu = menu;
        this.activo = activo;
    }

    public int getIdRecompensa() {
        return idRecompensa;
    }

    public void setIdRecompensa(int idRecompensa) {
        this.idRecompensa = idRecompensa;
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

    public int getPuntosRequeridos() {
        return puntosRequeridos;
    }

    public void setPuntosRequeridos(int puntosRequeridos) {
        this.puntosRequeridos = puntosRequeridos;
    }

    public String getTipoRecompensa() {
        return tipoRecompensa;
    }

    public void setTipoRecompensa(String tipoRecompensa) {
        this.tipoRecompensa = tipoRecompensa;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}