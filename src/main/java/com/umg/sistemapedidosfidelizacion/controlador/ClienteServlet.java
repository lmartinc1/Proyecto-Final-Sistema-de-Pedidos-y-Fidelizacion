package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.dao.ClienteDAO;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;
import com.umg.sistemapedidosfidelizacion.servicio.FidelizacionServicio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final FidelizacionServicio fidelizacionServicio = new FidelizacionServicio();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            if ("nuevo".equals(action)) {

                request.setAttribute(
                        "codigoSiguiente",
                        clienteDAO.generarSiguienteCodigo()
                );

                request.getRequestDispatcher(
                        "/views/clientes/formulario.jsp"
                ).forward(request, response);

                return;
            }

            if ("editar".equals(action)) {

                int id = Integer.parseInt(
                        request.getParameter("id")
                );

                request.setAttribute(
                        "cliente",
                        clienteDAO.buscarPorId(id)
                );

                request.getRequestDispatcher(
                        "/views/clientes/formulario.jsp"
                ).forward(request, response);

                return;
            }

            if ("historial".equals(action)) {

                int id = Integer.parseInt(
                        request.getParameter("id")
                );

                Cliente cliente =
                        clienteDAO.buscarPorId(id);

                if (cliente == null) {
                    response.sendRedirect(
                            request.getContextPath()
                            + "/clientes"
                    );
                    return;
                }

                request.setAttribute(
                        "cliente",
                        cliente
                );

                request.setAttribute(
                        "movimientos",
                        fidelizacionServicio.listarMovimientos(
                                cliente.getCodigo()
                        )
                );

                request.setAttribute(
                        "canjes",
                        fidelizacionServicio.listarCanjes(
                                cliente.getCodigo()
                        )
                );

                request.getRequestDispatcher(
                        "/views/clientes/historial.jsp"
                ).forward(request, response);

                return;
            }

            request.setAttribute(
                    "clientes",
                    clienteDAO.listar()
            );

            request.getRequestDispatcher(
                    "/views/clientes/listar.jsp"
            ).forward(request, response);

        } catch (SQLException | NumberFormatException e) {

            throw new ServletException(
                    "No se pudo procesar la solicitud de clientes.",
                    e
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            if ("desactivar".equals(action)) {

                int id = Integer.parseInt(
                        request.getParameter("id")
                );

                clienteDAO.desactivar(id);

                response.sendRedirect(
                        request.getContextPath()
                        + "/clientes"
                );

                return;
            }

            String idTexto =
                    request.getParameter("idCliente");

            String codigo =
                    request.getParameter("codigo");

            String nombre =
                    request.getParameter("nombre");

            String telefono =
                    request.getParameter("telefono");

            if (codigo == null
                    || codigo.isBlank()
                    || nombre == null
                    || nombre.isBlank()) {

                request.setAttribute(
                        "error",
                        "Código y nombre son obligatorios."
                );

                request.getRequestDispatcher(
                        "/views/clientes/formulario.jsp"
                ).forward(request, response);

                return;
            }

            if (idTexto == null || idTexto.isBlank()) {

                if (clienteDAO.existeCodigo(codigo)) {

                    request.setAttribute(
                            "error",
                            "El código del cliente ya existe."
                    );

                    request.getRequestDispatcher(
                            "/views/clientes/formulario.jsp"
                    ).forward(request, response);

                    return;
                }

                Cliente cliente =
                        new Cliente(
                                0,
                                codigo.trim().toUpperCase(),
                                nombre.trim(),
                                telefono == null
                                        ? ""
                                        : telefono.trim(),
                                0,
                                true
                        );

                clienteDAO.insertar(cliente);

            } else {

                int id =
                        Integer.parseInt(idTexto);

                Cliente cliente =
                        clienteDAO.buscarPorId(id);

                if (cliente != null) {

                    cliente.setNombre(
                            nombre.trim()
                    );

                    cliente.setTelefono(
                            telefono == null
                                    ? ""
                                    : telefono.trim()
                    );

                    clienteDAO.actualizar(cliente);
                }
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/clientes"
            );

        } catch (SQLException | NumberFormatException e) {

            throw new ServletException(
                    "No se pudo guardar el cliente.",
                    e
            );
        }
    }
}