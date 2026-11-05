package com.umg.sistemapedidosfidelizacion.modelo;

public class Cliente extends Persona {

    private String codigo;
    private int saldoPuntos;
    private boolean activo;

    public Cliente() {
    }

    public Cliente(int idCliente, String codigo, String nombre, String telefono,
                   int saldoPuntos, boolean activo) {
        super(idCliente, nombre, telefono);
        this.codigo = codigo;
        this.saldoPuntos = saldoPuntos;
        this.activo = activo;
    }

    public int getIdCliente() {
        return getId();
    }

    public void setIdCliente(int idCliente) {
        setId(idCliente);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getSaldoPuntos() {
        return saldoPuntos;
    }

    public void setSaldoPuntos(int saldoPuntos) {
        this.saldoPuntos = saldoPuntos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}