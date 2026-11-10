package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.dao.ClienteDAO;
import com.umg.sistemapedidosfidelizacion.excepcion.InventarioInsuficienteException;
import com.umg.sistemapedidosfidelizacion.excepcion.PuntosInsuficientesException;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;
import com.umg.sistemapedidosfidelizacion.servicio.FidelizacionServicio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/fidelizacion")
public class FidelizacionControlador {

    private final ClienteDAO clienteDAO =
            new ClienteDAO();

    private final FidelizacionServicio servicio =
            new FidelizacionServicio();

    @GetMapping
    public String mostrar(
            @RequestParam(
                    name = "ok",
                    required = false
            ) String ok,
            Model model
    ) {

        cargarDatos(
                model
        );

        model.addAttribute(
                "ok",
                "1".equals(ok)
        );

        return "fidelizacion/canjear";
    }

    @GetMapping("/historial/{codigo}")
    public String historial(
            @PathVariable String codigo,
            Model model
    ) {

        try {
            Cliente cliente =
                    clienteDAO.buscarPorCodigo(
                            codigo
                    );

            if (cliente == null) {
                return "redirect:/fidelizacion";
            }

            model.addAttribute(
                    "cliente",
                    cliente
            );

            model.addAttribute(
                    "movimientos",
                    servicio.listarMovimientos(
                            codigo
                    )
            );

            model.addAttribute(
                    "canjes",
                    servicio.listarCanjes(
                            codigo
                    )
            );

            return "fidelizacion/historial";

        } catch (SQLException
                 | IllegalArgumentException e) {

            return "redirect:/fidelizacion";
        }
    }

    @PostMapping("/canjear")
    public String canjear(
            @RequestParam String codigoCliente,
            @RequestParam String codigoRecompensa,
            Model model
    ) {

        try {
            servicio.canjear(
                    codigoCliente,
                    codigoRecompensa
            );

            return "redirect:/fidelizacion?ok=1";

        } catch (SQLException
                 | InventarioInsuficienteException
                 | PuntosInsuficientesException
                 | IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            cargarDatos(
                    model
            );

            return "fidelizacion/canjear";
        }
    }

    private void cargarDatos(
            Model model
    ) {

        try {
            List<Cliente> activos =
                    new ArrayList<>();

            for (Cliente cliente
                    : clienteDAO.listar()) {

                if (cliente.isActivo()) {
                    activos.add(
                            cliente
                    );
                }
            }

            model.addAttribute(
                    "clientes",
                    activos
            );

            model.addAttribute(
                    "recompensas",
                    servicio.listarRecompensas()
            );

        } catch (SQLException e) {

            model.addAttribute(
                    "clientes",
                    new ArrayList<>()
            );

            model.addAttribute(
                    "recompensas",
                    new ArrayList<>()
            );

            if (!model.containsAttribute("error")) {
                model.addAttribute(
                        "error",
                        "No se pudo cargar fidelización."
                );
            }
        }
    }
}
