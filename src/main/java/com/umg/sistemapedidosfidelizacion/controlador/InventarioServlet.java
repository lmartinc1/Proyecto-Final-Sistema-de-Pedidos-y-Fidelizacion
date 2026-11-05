package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.dao.InventarioDAO;
import com.umg.sistemapedidosfidelizacion.dao.ProductoDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/inventario")
public class InventarioServlet extends HttpServlet {

    private final InventarioDAO inventarioDAO =
            new InventarioDAO();

    private final ProductoDAO productoDAO =
            new ProductoDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        cargarDatos(request);

        request.getRequestDispatcher(
                "/views/inventario/abastecer.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            int idProducto =
                    Integer.parseInt(
                            request.getParameter("idProducto")
                    );

            int cantidad =
                    Integer.parseInt(
                            request.getParameter("cantidad")
                    );

            String referencia =
                    request.getParameter("referencia");

            inventarioDAO.abastecer(
                    idProducto,
                    cantidad,
                    referencia
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/inventario?ok=1"
            );

        } catch (SQLException | IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            cargarDatos(request);

            request.getRequestDispatcher(
                    "/views/inventario/abastecer.jsp"
            ).forward(request, response);
        }
    }

    private void cargarDatos(
            HttpServletRequest request
    ) throws ServletException {

        try {
            request.setAttribute(
                    "productos",
                    productoDAO.listarActivos()
            );

            request.setAttribute(
                    "movimientos",
                    inventarioDAO.listarMovimientos()
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "No se pudo cargar el inventario.",
                    e
            );
        }
    }
}