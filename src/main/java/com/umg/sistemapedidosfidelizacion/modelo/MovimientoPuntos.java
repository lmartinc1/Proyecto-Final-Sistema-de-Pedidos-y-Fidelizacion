package com.umg.sistemapedidosfidelizacion.modelo;

import java.time.LocalDateTime;

public class MovimientoPuntos {

    private int idMovimiento;
    private Cliente cliente;
    private LocalDateTime fecha;
    private String tipo;
    private int puntos;
    private int saldoResultante;
    private Integer idPedido;
    private Integer idCanje;

    public MovimientoPuntos() {
    }

    public MovimientoPuntos(int idMovimiento, Cliente cliente,
                            LocalDateTime fecha, String tipo, int puntos,
                            int saldoResultante, Integer idPedido,
                            Integer idCanje) {
        this.idMovimiento = idMovimiento;
        this.cliente = cliente;
        this.fecha = fecha;
        this.tipo = tipo;
        this.puntos = puntos;
        this.saldoResultante = saldoResultante;
        this.idPedido = idPedido;
        this.idCanje = idCanje;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(int saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public Integer getIdCanje() {
        return idCanje;
    }

    public void setIdCanje(Integer idCanje) {
        this.idCanje = idCanje;
    }
}