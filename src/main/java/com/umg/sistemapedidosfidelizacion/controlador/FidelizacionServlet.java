package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.dao.ClienteDAO;
import com.umg.sistemapedidosfidelizacion.excepcion.InventarioInsuficienteException;
import com.umg.sistemapedidosfidelizacion.excepcion.PuntosInsuficientesException;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;
import com.umg.sistemapedidosfidelizacion.servicio.FidelizacionServicio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/fidelizacion")
public class FidelizacionServlet extends HttpServlet {

    private final ClienteDAO clienteDAO =
            new ClienteDAO();

    private final FidelizacionServicio servicio =
            new FidelizacionServicio();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String action =
                request.getParameter("action");

        try {
            if ("historial".equals(action)) {

                String codigo =
                        request.getParameter("codigo");

                Cliente cliente =
                        clienteDAO.buscarPorCodigo(
                                codigo
                        );

                request.setAttribute(
                        "cliente",
                        cliente
                );

                request.setAttribute(
                        "movimientos",
                        servicio.listarMovimientos(
                                codigo
                        )
                );

                request.setAttribute(
                        "canjes",
                        servicio.listarCanjes(
                                codigo
                        )
                );

                request.getRequestDispatcher(
                        "/views/fidelizacion/historial.jsp"
                ).forward(request, response);

                return;
            }

            cargarDatos(request);

            request.getRequestDispatcher(
                    "/views/fidelizacion/canjear.jsp"
            ).forward(request, response);

        } catch (SQLException
                 | IllegalArgumentException e) {

            throw new ServletException(
                    "No se pudo cargar fidelización.",
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

        String codigoCliente =
                request.getParameter("codigoCliente");

        String codigoRecompensa =
                request.getParameter("codigoRecompensa");

        try {
            servicio.canjear(
                    codigoCliente,
                    codigoRecompensa
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/fidelizacion?ok=1"
            );

        } catch (SQLException
                 | InventarioInsuficienteException
                 | PuntosInsuficientesException
                 | IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            cargarDatos(request);

            request.getRequestDispatcher(
                    "/views/fidelizacion/canjear.jsp"
            ).forward(request, response);
        }
    }

    private void cargarDatos(
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
                    "recompensas",
                    servicio.listarRecompensas()
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "No se pudo cargar fidelización.",
                    e
            );
        }
    }
}