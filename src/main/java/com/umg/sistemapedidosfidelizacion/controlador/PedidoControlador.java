package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.dao.ClienteDAO;
import com.umg.sistemapedidosfidelizacion.dao.MenuDAO;
import com.umg.sistemapedidosfidelizacion.dao.PedidoDAO;
import com.umg.sistemapedidosfidelizacion.dao.ProductoDAO;
import com.umg.sistemapedidosfidelizacion.excepcion.InventarioInsuficienteException;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;
import com.umg.sistemapedidosfidelizacion.modelo.DetallePedido;
import com.umg.sistemapedidosfidelizacion.modelo.Menu;
import com.umg.sistemapedidosfidelizacion.modelo.Pedido;
import com.umg.sistemapedidosfidelizacion.modelo.Producto;
import com.umg.sistemapedidosfidelizacion.servicio.PedidoServicio;

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
import java.util.Map;

@Controller
@RequestMapping("/pedidos")
public class PedidoControlador {

    private final PedidoServicio pedidoServicio =
            new PedidoServicio();

    private final PedidoDAO pedidoDAO =
            new PedidoDAO();

    private final ClienteDAO clienteDAO =
            new ClienteDAO();

    private final ProductoDAO productoDAO =
            new ProductoDAO();

    private final MenuDAO menuDAO =
            new MenuDAO();

    @GetMapping
    public String listar(Model model) {

        try {
            model.addAttribute(
                    "pedidos",
                    pedidoServicio.listar()
            );

        } catch (SQLException e) {

            model.addAttribute(
                    "pedidos",
                    new ArrayList<>()
            );

            model.addAttribute(
                    "error",
                    "No se pudieron cargar los pedidos."
            );
        }

        return "pedidos/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        cargarFormulario(
                model
        );

        return "pedidos/nuevo";
    }

    @GetMapping("/{id}")
    public String detalle(
            @PathVariable int id,
            Model model
    ) {

        try {
            Pedido pedido =
                    pedidoServicio.buscarPorId(id);

            if (pedido == null) {
                return "redirect:/pedidos";
            }

            model.addAttribute(
                    "pedido",
                    pedido
            );

            return "pedidos/detalle";

        } catch (SQLException e) {

            return "redirect:/pedidos";
        }
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam Map<String, String> parametros,
            Model model
    ) {

        try {
            String codigoCliente =
                    parametros.get(
                            "codigoCliente"
                    );

            List<DetallePedido> detalles =
                    new ArrayList<>();

            for (Producto producto
                    : productoDAO.listarActivos()) {

                int cantidad =
                        convertirCantidad(
                                parametros.get(
                                        "producto_"
                                        + producto.getIdProducto()
                                )
                        );

                if (cantidad > 0) {

                    detalles.add(
                            new DetallePedido(
                                    "PRODUCTO",
                                    producto,
                                    null,
                                    cantidad,
                                    producto.getPrecio()
                            )
                    );
                }
            }

            for (Menu menu
                    : menuDAO.listarActivos()) {

                int cantidad =
                        convertirCantidad(
                                parametros.get(
                                        "menu_"
                                        + menu.getIdMenu()
                                )
                        );

                if (cantidad > 0) {

                    detalles.add(
                            new DetallePedido(
                                    "MENU",
                                    null,
                                    menu,
                                    cantidad,
                                    menu.getPrecio()
                            )
                    );
                }
            }

            Pedido pedido =
                    pedidoServicio.crearPedidoPendiente(
                            codigoCliente,
                            detalles
                    );

            return "redirect:/pedidos/"
                    + pedido.getIdPedido();

        } catch (SQLException
                 | InventarioInsuficienteException
                 | IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            cargarFormulario(
                    model
            );

            return "pedidos/nuevo";
        }
    }

    @PostMapping("/anular/{id}")
    public String anular(
            @PathVariable int id,
            Model model
    ) {

        try {
            boolean anulado =
                    pedidoServicio.anularPedido(id);

            if (!anulado) {
                return listarConError(
                        model,
                        "Solo se puede anular un pedido pendiente."
                );
            }

            return "redirect:/pedidos";

        } catch (SQLException e) {

            return listarConError(
                    model,
                    "No se pudo anular el pedido."
            );
        }
    }

    @PostMapping("/entregar/{id}")
    public String entregar(
            @PathVariable int id,
            Model model
    ) {

        try {
            Pedido pedido =
                    pedidoServicio.buscarPorId(id);

            if (pedido == null
                    || !"PAGADO".equalsIgnoreCase(
                            pedido.getEstado()
                    )) {

                return listarConError(
                        model,
                        "Solo se puede entregar un pedido pagado."
                );
            }

            pedidoDAO.actualizarEstado(
                    id,
                    "ENTREGADO"
            );

            return "redirect:/pedidos";

        } catch (SQLException e) {

            return listarConError(
                    model,
                    "No se pudo actualizar el pedido."
            );
        }
    }

    private int convertirCantidad(
            String valor
    ) {

        if (valor == null
                || valor.isBlank()) {

            return 0;
        }

        try {
            int cantidad =
                    Integer.parseInt(
                            valor
                    );

            return Math.max(
                    cantidad,
                    0
            );

        } catch (NumberFormatException e) {

            return 0;
        }
    }

    private void cargarFormulario(
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
                    "productos",
                    productoDAO.listarActivos()
            );

            model.addAttribute(
                    "menus",
                    menuDAO.listarActivos()
            );

        } catch (SQLException e) {

            model.addAttribute(
                    "clientes",
                    new ArrayList<>()
            );

            model.addAttribute(
                    "productos",
                    new ArrayList<>()
            );

            model.addAttribute(
                    "menus",
                    new ArrayList<>()
            );

            if (!model.containsAttribute("error")) {
                model.addAttribute(
                        "error",
                        "No se pudo cargar el formulario del pedido."
                );
            }
        }
    }

    private String listarConError(
            Model model,
            String mensaje
    ) {

        try {
            model.addAttribute(
                    "pedidos",
                    pedidoServicio.listar()
            );

        } catch (SQLException e) {

            model.addAttribute(
                    "pedidos",
                    new ArrayList<>()
            );
        }

        model.addAttribute(
                "error",
                mensaje
        );

        return "pedidos/listar";
    }
}
