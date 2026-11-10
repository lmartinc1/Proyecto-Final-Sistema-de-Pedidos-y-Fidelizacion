package com.umg.sistemapedidosfidelizacion.servicio;

import com.umg.sistemapedidosfidelizacion.config.ConexionDB;
import com.umg.sistemapedidosfidelizacion.dao.FidelizacionDAO;
import com.umg.sistemapedidosfidelizacion.dao.InventarioDAO;
import com.umg.sistemapedidosfidelizacion.dao.PagoDAO;
import com.umg.sistemapedidosfidelizacion.dao.PedidoDAO;
import com.umg.sistemapedidosfidelizacion.excepcion.InventarioInsuficienteException;
import com.umg.sistemapedidosfidelizacion.modelo.DetallePedido;
import com.umg.sistemapedidosfidelizacion.modelo.Menu;
import com.umg.sistemapedidosfidelizacion.modelo.MetodoPago;
import com.umg.sistemapedidosfidelizacion.modelo.PagoEfectivo;
import com.umg.sistemapedidosfidelizacion.modelo.PagoTarjeta;
import com.umg.sistemapedidosfidelizacion.modelo.Pedido;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class VentaServicio {

    private final PedidoDAO pedidoDAO;
    private final PagoDAO pagoDAO;
    private final InventarioDAO inventarioDAO;
    private final FidelizacionDAO fidelizacionDAO;

    public VentaServicio() {
        this.pedidoDAO = new PedidoDAO();
        this.pagoDAO = new PagoDAO();
        this.inventarioDAO = new InventarioDAO();
        this.fidelizacionDAO = new FidelizacionDAO();
    }

    public PagoEfectivo pagarEfectivo(
            int idPedido,
            BigDecimal montoRecibido
    ) throws SQLException, InventarioInsuficienteException {

        Pedido pedido =
                obtenerPedido(idPedido);

        PagoEfectivo pago =
                new PagoEfectivo(
                        0,
                        pedido,
                        LocalDateTime.now(),
                        pedido.getTotal(),
                        "APROBADO",
                        montoRecibido,
                        BigDecimal.ZERO
                );

        if (!pago.validarPago()) {
            throw new IllegalArgumentException(
                    "Monto insuficiente. Total a pagar: Q"
                    + pedido.getTotal()
            );
        }

        pago.calcularCambio();

        procesarPagoAprobado(
                pedido,
                pago
        );

        return pago;
    }

    public PagoTarjeta pagarTarjeta(
            int idPedido,
            boolean aprobado
    ) throws SQLException, InventarioInsuficienteException {

        Pedido pedido =
                obtenerPedido(idPedido);

        if (!aprobado) {

            return new PagoTarjeta(
                    0,
                    pedido,
                    LocalDateTime.now(),
                    pedido.getTotal(),
                    "RECHAZADO",
                    null
            );
        }

        String referencia =
                generarReferenciaTarjeta(idPedido);

        PagoTarjeta pago =
                new PagoTarjeta(
                        0,
                        pedido,
                        LocalDateTime.now(),
                        pedido.getTotal(),
                        "APROBADO",
                        referencia
                );

        procesarPagoAprobado(
                pedido,
                pago
        );

        return pago;
    }

    private Pedido obtenerPedido(int idPedido)
            throws SQLException {

        Pedido pedido =
                pedidoDAO.buscarPorId(idPedido);

        if (pedido == null) {
            throw new IllegalArgumentException(
                    "El pedido no existe."
            );
        }

        return pedido;
    }

    private void procesarPagoAprobado(
            Pedido pedido,
            MetodoPago pago
    ) throws SQLException, InventarioInsuficienteException {

        try (Connection conexion =
                     ConexionDB.getConnection()) {

            boolean autoCommitOriginal =
                    conexion.getAutoCommit();

            conexion.setAutoCommit(false);

            try {
                String estadoActual =
                        pedidoDAO.obtenerEstadoParaActualizar(
                                conexion,
                                pedido.getIdPedido()
                        );

                if (estadoActual == null) {
                    throw new IllegalArgumentException(
                            "El pedido no existe."
                    );
                }

                if (!"PENDIENTE".equalsIgnoreCase(
                        estadoActual
                )) {

                    throw new IllegalArgumentException(
                            "El pedido ya no está pendiente."
                    );
                }

                descontarInventario(
                        conexion,
                        pedido
                );

                pagoDAO.insertar(
                        conexion,
                        pago
                );

                int puntos =
                        calcularPuntos(pedido);

                int nuevoSaldo =
                        pedido.getCliente()
                                .getSaldoPuntos();

                if (puntos > 0) {

                    nuevoSaldo =
                            fidelizacionDAO.acreditarPuntos(
                                    conexion,
                                    pedido.getCliente()
                                            .getIdCliente(),
                                    puntos,
                                    pedido.getIdPedido()
                            );
                }

                boolean actualizado =
                        pedidoDAO.actualizarEstado(
                                conexion,
                                pedido.getIdPedido(),
                                "PAGADO"
                        );

                if (!actualizado) {
                    throw new SQLException(
                            "No se pudo actualizar el estado del pedido."
                    );
                }

                conexion.commit();

                pedido.setEstado("PAGADO");

                pedido.getCliente()
                        .setSaldoPuntos(nuevoSaldo);

            } catch (SQLException
                     | InventarioInsuficienteException
                     | RuntimeException e) {

                conexion.rollback();

                throw e;

            } finally {

                conexion.setAutoCommit(
                        autoCommitOriginal
                );
            }
        }
    }

    private void descontarInventario(
            Connection conexion,
            Pedido pedido
    ) throws SQLException, InventarioInsuficienteException {

        Map<Integer, Integer> cantidades =
                new TreeMap<>();

        for (DetallePedido detalle
                : pedido.getDetalles()) {

            int cantidad =
                    detalle.getCantidad();

            if ("PRODUCTO".equalsIgnoreCase(
                    detalle.getTipoItem()
            )) {

                agregarCantidad(
                        cantidades,
                        detalle.getProducto()
                                .getIdProducto(),
                        cantidad
                );

            } else if ("MENU".equalsIgnoreCase(
                    detalle.getTipoItem()
            )) {

                Menu menu =
                        detalle.getMenu();

                agregarCantidad(
                        cantidades,
                        menu.getSandwich()
                                .getIdProducto(),
                        cantidad
                );

                agregarCantidad(
                        cantidades,
                        menu.getBebida()
                                .getIdProducto(),
                        cantidad
                );

                agregarCantidad(
                        cantidades,
                        menu.getAcompanamiento()
                                .getIdProducto(),
                        cantidad
                );
            }
        }

        String referencia =
                "Pedido #" + pedido.getIdPedido();

        for (Map.Entry<Integer, Integer> entrada
                : cantidades.entrySet()) {

            inventarioDAO.descontar(
                    conexion,
                    entrada.getKey(),
                    entrada.getValue(),
                    "VENTA",
                    referencia
            );
        }
    }

    private int calcularPuntos(Pedido pedido) {

        int puntos = 0;

        for (DetallePedido detalle
                : pedido.getDetalles()) {

            if ("MENU".equalsIgnoreCase(
                    detalle.getTipoItem()
            )) {

                puntos +=
                        detalle.getCantidad() * 8;

            } else if (
                    "PRODUCTO".equalsIgnoreCase(
                            detalle.getTipoItem()
                    )
                    && detalle.getProducto() != null
                    && "SANDWICH".equalsIgnoreCase(
                            detalle.getProducto()
                                    .getCategoria()
                    )
            ) {

                puntos +=
                        detalle.getCantidad() * 2;
            }
        }

        return puntos;
    }

    private void agregarCantidad(
            Map<Integer, Integer> cantidades,
            int idProducto,
            int cantidad
    ) {

        cantidades.merge(
                idProducto,
                cantidad,
                Integer::sum
        );
    }

    private String generarReferenciaTarjeta(
            int idPedido
    ) {

        return "TAR-"
                + String.format("%06d", idPedido)
                + "-"
                + System.currentTimeMillis();
    }
}