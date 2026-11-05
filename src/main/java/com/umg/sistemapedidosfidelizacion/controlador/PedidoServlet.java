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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/pedidos")
public class PedidoServlet extends HttpServlet {

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

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action =
                request.getParameter("action");

        try {
            if ("nuevo".equals(action)) {

                cargarFormulario(request);

                request.getRequestDispatcher(
                        "/views/pedidos/nuevo.jsp"
                ).forward(request, response);

                return;
            }

            if ("detalle".equals(action)) {

                int id =
                        Integer.parseInt(
                                request.getParameter("id")
                        );

                request.setAttribute(
                        "pedido",
                        pedidoServicio.buscarPorId(id)
                );

                request.getRequestDispatcher(
                        "/views/pedidos/detalle.jsp"
                ).forward(request, response);

                return;
            }

            request.setAttribute(
                    "pedidos",
                    pedidoServicio.listar()
            );

            request.getRequestDispatcher(
                    "/views/pedidos/listar.jsp"
            ).forward(request, response);

        } catch (SQLException | NumberFormatException e) {

            throw new ServletException(
                    "No se pudo procesar la solicitud de pedidos.",
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

        String action =
                request.getParameter("action");

        try {
            if ("anular".equals(action)) {

                int id =
                        Integer.parseInt(
                                request.getParameter("id")
                        );

                pedidoServicio.anularPedido(id);

                response.sendRedirect(
                        request.getContextPath()
                        + "/pedidos"
                );

                return;
            }

            if ("entregar".equals(action)) {

                int id =
                        Integer.parseInt(
                                request.getParameter("id")
                        );

                Pedido pedido =
                        pedidoServicio.buscarPorId(id);

                if (pedido != null
                        && "PAGADO".equalsIgnoreCase(
                                pedido.getEstado()
                        )) {

                    pedidoDAO.actualizarEstado(
                            id,
                            "ENTREGADO"
                    );
                }

                response.sendRedirect(
                        request.getContextPath()
                        + "/pedidos"
                );

                return;
            }

            String codigoCliente =
                    request.getParameter("codigoCliente");

            List<DetallePedido> detalles =
                    new ArrayList<>();

            for (Producto producto
                    : productoDAO.listarActivos()) {

                String valor =
                        request.getParameter(
                                "producto_"
                                + producto.getIdProducto()
                        );

                int cantidad =
                        convertirCantidad(valor);

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

                String valor =
                        request.getParameter(
                                "menu_"
                                + menu.getIdMenu()
                        );

                int cantidad =
                        convertirCantidad(valor);

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

            response.sendRedirect(
                    request.getContextPath()
                    + "/pedidos?action=detalle&id="
                    + pedido.getIdPedido()
            );

        } catch (SQLException
                 | InventarioInsuficienteException
                 | IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            cargarFormulario(request);

            request.getRequestDispatcher(
                    "/views/pedidos/nuevo.jsp"
            ).forward(request, response);
        }
    }

    private int convertirCantidad(String valor) {

        if (valor == null || valor.isBlank()) {
            return 0;
        }

        try {
            int cantidad =
                    Integer.parseInt(valor);

            return Math.max(cantidad, 0);

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void cargarFormulario(
            HttpServletRequest request
    ) throws ServletException {

        try {
            List<Cliente> activos =
                    new ArrayList<>();

            for (Cliente cliente
                    : clienteDAO.listar()) {

                if (cliente.isActivo()) {
                    activos.add(cliente);
                }
            }

            request.setAttribute(
                    "clientes",
                    activos
            );

            request.setAttribute(
                    "productos",
                    productoDAO.listarActivos()
            );

            request.setAttribute(
                    "menus",
                    menuDAO.listarActivos()
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "No se pudo cargar el formulario del pedido.",
                    e
            );
        }
    }
}