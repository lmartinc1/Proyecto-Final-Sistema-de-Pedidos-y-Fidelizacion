package com.umg.sistemapedidosfidelizacion.excepcion;

public class InventarioInsuficienteException extends Exception {

    public InventarioInsuficienteException(String mensaje) {
        super(mensaje);
    }
}