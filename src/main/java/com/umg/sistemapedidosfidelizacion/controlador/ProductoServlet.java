package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.dao.ProductoDAO;
import com.umg.sistemapedidosfidelizacion.modelo.Producto;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/productos")
public class ProductoServlet extends HttpServlet {

    private final ProductoDAO productoDAO =
            new ProductoDAO();

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

                request.getRequestDispatcher(
                        "/views/productos/formulario.jsp"
                ).forward(request, response);

                return;
            }

            if ("editar".equals(action)) {

                int id =
                        Integer.parseInt(
                                request.getParameter("id")
                        );

                request.setAttribute(
                        "producto",
                        productoDAO.buscarPorId(id)
                );

                request.getRequestDispatcher(
                        "/views/productos/formulario.jsp"
                ).forward(request, response);

                return;
            }

            request.setAttribute(
                    "productos",
                    productoDAO.listar()
            );

            request.getRequestDispatcher(
                    "/views/productos/listar.jsp"
            ).forward(request, response);

        } catch (SQLException | IllegalArgumentException e) {

            throw new ServletException(
                    "No se pudo procesar la solicitud de productos.",
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
            if ("desactivar".equals(action)) {

                int id =
                        Integer.parseInt(
                                request.getParameter("id")
                        );

                productoDAO.desactivar(id);

                response.sendRedirect(
                        request.getContextPath()
                        + "/productos"
                );

                return;
            }

            String idTexto =
                    request.getParameter("idProducto");

            String codigo =
                    request.getParameter("codigo");

            String categoria =
                    request.getParameter("categoria");

            String nombre =
                    request.getParameter("nombre");

            String precioTexto =
                    request.getParameter("precio");

            if (codigo == null
                    || codigo.isBlank()
                    || categoria == null
                    || categoria.isBlank()
                    || nombre == null
                    || nombre.isBlank()
                    || precioTexto == null
                    || precioTexto.isBlank()) {

                throw new IllegalArgumentException(
                        "Todos los campos son obligatorios."
                );
            }

            BigDecimal precio =
                    new BigDecimal(precioTexto);

            if (precio.compareTo(BigDecimal.ZERO) < 0) {

                throw new IllegalArgumentException(
                        "El precio no puede ser negativo."
                );
            }

            if (idTexto == null
                    || idTexto.isBlank()) {

                if (productoDAO.existeCodigo(codigo)) {

                    throw new IllegalArgumentException(
                            "El código del producto ya existe."
                    );
                }

                Producto producto =
                        new Producto(
                                0,
                                codigo.trim().toUpperCase(),
                                categoria,
                                nombre.trim(),
                                precio,
                                0,
                                true
                        );

                productoDAO.insertar(producto);

            } else {

                int id =
                        Integer.parseInt(idTexto);

                Producto producto =
                        productoDAO.buscarPorId(id);

                if (producto != null) {

                    producto.setCategoria(categoria);
                    producto.setNombre(nombre.trim());
                    producto.setPrecio(precio);

                    productoDAO.actualizar(producto);
                }
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/productos"
            );

        } catch (SQLException | IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            try {
                String idTexto =
                        request.getParameter("idProducto");

                if (idTexto != null
                        && !idTexto.isBlank()) {

                    request.setAttribute(
                            "producto",
                            productoDAO.buscarPorId(
                                    Integer.parseInt(idTexto)
                            )
                    );
                }

            } catch (Exception ignored) {
            }

            request.getRequestDispatcher(
                    "/views/productos/formulario.jsp"
            ).forward(request, response);
        }
    }
}