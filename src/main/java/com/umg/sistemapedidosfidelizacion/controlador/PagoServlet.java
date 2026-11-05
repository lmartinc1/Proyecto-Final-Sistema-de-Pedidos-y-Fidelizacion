package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.excepcion.InventarioInsuficienteException;
import com.umg.sistemapedidosfidelizacion.modelo.MetodoPago;
import com.umg.sistemapedidosfidelizacion.modelo.PagoTarjeta;
import com.umg.sistemapedidosfidelizacion.modelo.Pedido;
import com.umg.sistemapedidosfidelizacion.servicio.PedidoServicio;
import com.umg.sistemapedidosfidelizacion.servicio.VentaServicio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/pagos")
public class PagoServlet extends HttpServlet {

    private final PedidoServicio pedidoServicio =
            new PedidoServicio();

    private final VentaServicio ventaServicio =
            new VentaServicio();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            int idPedido =
                    Integer.parseInt(
                            request.getParameter("idPedido")
                    );

            Pedido pedido =
                    pedidoServicio.buscarPorId(
                            idPedido
                    );

            if (pedido == null) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/pedidos"
                );

                return;
            }

            request.setAttribute(
                    "pedido",
                    pedido
            );

            request.getRequestDispatcher(
                    "/views/pagos/cobrar.jsp"
            ).forward(request, response);

        } catch (SQLException | IllegalArgumentException e) {

            throw new ServletException(
                    "No se pudo cargar el pago.",
                    e
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            int idPedido =
                    Integer.parseInt(
                            request.getParameter("idPedido")
                    );

            String metodo =
                    request.getParameter("metodo");

            MetodoPago pago;

            if ("EFECTIVO".equalsIgnoreCase(metodo)) {

                String montoTexto =
                        request.getParameter(
                                "montoRecibido"
                        );

                if (montoTexto == null
                        || montoTexto.isBlank()) {

                    throw new IllegalArgumentException(
                            "Ingrese el monto recibido."
                    );
                }

                BigDecimal recibido =
                        new BigDecimal(
                                montoTexto
                        );

                pago =
                        ventaServicio.pagarEfectivo(
                                idPedido,
                                recibido
                        );

            } else if ("TARJETA".equalsIgnoreCase(metodo)) {

                boolean aprobado =
                        "APROBADO".equalsIgnoreCase(
                                request.getParameter(
                                        "resultadoTarjeta"
                                )
                        );

                PagoTarjeta pagoTarjeta =
                        ventaServicio.pagarTarjeta(
                                idPedido,
                                aprobado
                        );

                if (!pagoTarjeta.validarPago()) {

                    request.setAttribute(
                            "error",
                            "Pago con tarjeta rechazado."
                    );

                    request.setAttribute(
                            "pedido",
                            pedidoServicio.buscarPorId(
                                    idPedido
                            )
                    );

                    request.getRequestDispatcher(
                            "/views/pagos/cobrar.jsp"
                    ).forward(request, response);

                    return;
                }

                pago =
                        pagoTarjeta;

            } else {

                throw new IllegalArgumentException(
                        "Seleccione un método de pago."
                );
            }

            request.setAttribute(
                    "pago",
                    pago
            );

            request.getRequestDispatcher(
                    "/views/pagos/comprobante.jsp"
            ).forward(request, response);

        } catch (SQLException
                 | InventarioInsuficienteException
                 | IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            try {
                int idPedido =
                        Integer.parseInt(
                                request.getParameter("idPedido")
                        );

                request.setAttribute(
                        "pedido",
                        pedidoServicio.buscarPorId(
                                idPedido
                        )
                );

            } catch (Exception ignored) {
            }

            request.getRequestDispatcher(
                    "/views/pagos/cobrar.jsp"
            ).forward(request, response);
        }
    }
}