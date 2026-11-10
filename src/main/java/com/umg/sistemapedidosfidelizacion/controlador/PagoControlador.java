package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.excepcion.InventarioInsuficienteException;
import com.umg.sistemapedidosfidelizacion.modelo.MetodoPago;
import com.umg.sistemapedidosfidelizacion.modelo.PagoEfectivo;
import com.umg.sistemapedidosfidelizacion.modelo.PagoTarjeta;
import com.umg.sistemapedidosfidelizacion.modelo.Pedido;
import com.umg.sistemapedidosfidelizacion.servicio.PedidoServicio;
import com.umg.sistemapedidosfidelizacion.servicio.VentaServicio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.sql.SQLException;

@Controller
@RequestMapping("/pagos")
public class PagoControlador {

    private final PedidoServicio pedidoServicio =
            new PedidoServicio();

    private final VentaServicio ventaServicio =
            new VentaServicio();

    @GetMapping("/{idPedido}")
    public String cobrar(
            @PathVariable int idPedido,
            Model model
    ) {

        try {
            Pedido pedido =
                    pedidoServicio.buscarPorId(
                            idPedido
                    );

            if (pedido == null
                    || !"PENDIENTE".equalsIgnoreCase(
                            pedido.getEstado()
                    )) {

                return "redirect:/pedidos";
            }

            model.addAttribute(
                    "pedido",
                    pedido
            );

            return "pagos/cobrar";

        } catch (SQLException e) {

            return "redirect:/pedidos";
        }
    }

    @PostMapping("/procesar")
    public String procesar(
            @RequestParam String idPedido,
            @RequestParam String metodo,
            @RequestParam(
                    required = false,
                    defaultValue = ""
            ) String montoRecibido,
            @RequestParam(
                    required = false,
                    defaultValue = ""
            ) String resultadoTarjeta,
            Model model
    ) {

        int id;

        try {
            id =
                    Integer.parseInt(
                            idPedido
                    );

        } catch (NumberFormatException e) {

            return "redirect:/pedidos";
        }

        try {
            MetodoPago pago;

            if ("EFECTIVO".equalsIgnoreCase(
                    metodo
            )) {

                if (montoRecibido.isBlank()) {
                    throw new IllegalArgumentException(
                            "Ingrese el monto recibido."
                    );
                }

                BigDecimal recibido =
                        new BigDecimal(
                                montoRecibido
                        );

                PagoEfectivo efectivo =
                        ventaServicio.pagarEfectivo(
                                id,
                                recibido
                        );

                pago =
                        efectivo;

                model.addAttribute(
                        "esEfectivo",
                        true
                );

                model.addAttribute(
                        "pagoEfectivo",
                        efectivo
                );

            } else if ("TARJETA".equalsIgnoreCase(
                    metodo
            )) {

                boolean aprobado =
                        "APROBADO".equalsIgnoreCase(
                                resultadoTarjeta
                        );

                PagoTarjeta tarjeta =
                        ventaServicio.pagarTarjeta(
                                id,
                                aprobado
                        );

                if (!tarjeta.validarPago()) {

                    model.addAttribute(
                            "error",
                            "Pago con tarjeta rechazado."
                    );

                    cargarPedido(
                            id,
                            model
                    );

                    return "pagos/cobrar";
                }

                pago =
                        tarjeta;

                model.addAttribute(
                        "esEfectivo",
                        false
                );

            } else {

                throw new IllegalArgumentException(
                        "Seleccione un método de pago."
                );
            }

            model.addAttribute(
                    "pago",
                    pago
            );

            return "pagos/comprobante";

        } catch (NumberFormatException e) {

            model.addAttribute(
                    "error",
                    "El monto recibido no es válido."
            );

        } catch (SQLException
                 | InventarioInsuficienteException
                 | IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );
        }

        cargarPedido(
                id,
                model
        );

        return "pagos/cobrar";
    }

    private void cargarPedido(
            int idPedido,
            Model model
    ) {

        try {
            model.addAttribute(
                    "pedido",
                    pedidoServicio.buscarPorId(
                            idPedido
                    )
            );

        } catch (SQLException e) {

            model.addAttribute(
                    "error",
                    "No se pudo volver a cargar el pedido."
            );
        }
    }
}
