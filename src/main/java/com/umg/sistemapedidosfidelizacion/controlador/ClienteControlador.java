package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.dao.ClienteDAO;
import com.umg.sistemapedidosfidelizacion.dao.PedidoDAO;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;
import com.umg.sistemapedidosfidelizacion.servicio.FidelizacionServicio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.ArrayList;

@Controller
@RequestMapping("/clientes")
public class ClienteControlador {

    private final ClienteDAO clienteDAO =
            new ClienteDAO();

    private final PedidoDAO pedidoDAO =
            new PedidoDAO();

    private final FidelizacionServicio fidelizacionServicio =
            new FidelizacionServicio();

    @GetMapping
    public String listar(Model model) {

        try {
            model.addAttribute(
                    "clientes",
                    clienteDAO.listar()
            );

        } catch (SQLException e) {

            model.addAttribute(
                    "clientes",
                    new ArrayList<>()
            );

            model.addAttribute(
                    "error",
                    "No se pudieron cargar los clientes."
            );
        }

        return "clientes/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        Cliente cliente =
                new Cliente();

        cliente.setActivo(true);

        try {
            cliente.setCodigo(
                    clienteDAO.generarSiguienteCodigo()
            );

        } catch (SQLException e) {

            model.addAttribute(
                    "error",
                    "No se pudo generar el código del cliente."
            );
        }

        model.addAttribute(
                "cliente",
                cliente
        );

        return "clientes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable int id,
            Model model
    ) {

        try {
            Cliente cliente =
                    clienteDAO.buscarPorId(id);

            if (cliente == null) {
                return "redirect:/clientes";
            }

            model.addAttribute(
                    "cliente",
                    cliente
            );

            return "clientes/formulario";

        } catch (SQLException e) {

            return "redirect:/clientes";
        }
    }

    @GetMapping("/historial/{id}")
    public String historial(
            @PathVariable int id,
            Model model
    ) {

        try {
            Cliente cliente =
                    clienteDAO.buscarPorId(id);

            if (cliente == null) {
                return "redirect:/clientes";
            }

            model.addAttribute(
                    "cliente",
                    cliente
            );

            model.addAttribute(
                    "pedidos",
                    pedidoDAO.listarPorCliente(
                            cliente.getIdCliente()
                    )
            );

            model.addAttribute(
                    "movimientos",
                    fidelizacionServicio.listarMovimientos(
                            cliente.getCodigo()
                    )
            );

            model.addAttribute(
                    "canjes",
                    fidelizacionServicio.listarCanjes(
                            cliente.getCodigo()
                    )
            );

            return "clientes/historial";

        } catch (SQLException | IllegalArgumentException e) {

            return "redirect:/clientes";
        }
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("cliente") Cliente cliente,
            Model model
    ) {

        try {
            validarCliente(cliente);

            if (cliente.getIdCliente() <= 0) {

                cliente.setCodigo(
                        cliente.getCodigo()
                                .trim()
                                .toUpperCase()
                );

                if (clienteDAO.existeCodigo(
                        cliente.getCodigo()
                )) {

                    throw new IllegalArgumentException(
                            "El código del cliente ya existe."
                    );
                }

                cliente.setSaldoPuntos(0);
                cliente.setActivo(true);

                clienteDAO.insertar(
                        cliente
                );

            } else {

                Cliente actual =
                        clienteDAO.buscarPorId(
                                cliente.getIdCliente()
                        );

                if (actual == null) {
                    throw new IllegalArgumentException(
                            "El cliente no existe."
                    );
                }

                actual.setNombre(
                        cliente.getNombre().trim()
                );

                actual.setTelefono(
                        cliente.getTelefono() == null
                                ? ""
                                : cliente.getTelefono().trim()
                );

                clienteDAO.actualizar(
                        actual
                );
            }

            return "redirect:/clientes";

        } catch (SQLException
                 | IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "clientes/formulario";
        }
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(
            @PathVariable int id,
            Model model
    ) {

        try {
            clienteDAO.desactivar(id);

            return "redirect:/clientes";

        } catch (SQLException e) {

            return listarConError(
                    model,
                    "No se pudo desactivar el cliente."
            );
        }
    }

    private void validarCliente(
            Cliente cliente
    ) {

        if (cliente.getCodigo() == null
                || cliente.getCodigo().isBlank()
                || cliente.getNombre() == null
                || cliente.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "Código y nombre son obligatorios."
            );
        }
    }

    private String listarConError(
            Model model,
            String mensaje
    ) {

        try {
            model.addAttribute(
                    "clientes",
                    clienteDAO.listar()
            );

        } catch (SQLException e) {
            model.addAttribute(
                    "clientes",
                    new ArrayList<>()
            );
        }

        model.addAttribute(
                "error",
                mensaje
        );

        return "clientes/listar";
    }
}
