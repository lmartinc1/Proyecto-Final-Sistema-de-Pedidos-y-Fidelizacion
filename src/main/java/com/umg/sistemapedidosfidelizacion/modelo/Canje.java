package com.umg.sistemapedidosfidelizacion.modelo;

import java.time.LocalDateTime;

public class Canje {

    private int idCanje;
    private Cliente cliente;
    private Recompensa recompensa;
    private LocalDateTime fecha;
    private int puntosUtilizados;

    public Canje() {
    }

    public Canje(int idCanje, Cliente cliente, Recompensa recompensa,
                 LocalDateTime fecha, int puntosUtilizados) {
        this.idCanje = idCanje;
        this.cliente = cliente;
        this.recompensa = recompensa;
        this.fecha = fecha;
        this.puntosUtilizados = puntosUtilizados;
    }

    public int getIdCanje() {
        return idCanje;
    }

    public void setIdCanje(int idCanje) {
        this.idCanje = idCanje;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Recompensa getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(Recompensa recompensa) {
        this.recompensa = recompensa;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getPuntosUtilizados() {
        return puntosUtilizados;
    }

    public void setPuntosUtilizados(int puntosUtilizados) {
        this.puntosUtilizados = puntosUtilizados;
    }
}