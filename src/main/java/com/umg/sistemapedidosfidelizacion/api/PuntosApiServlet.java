package com.umg.sistemapedidosfidelizacion.api;

import com.umg.sistemapedidosfidelizacion.dao.ClienteDAO;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/api/puntos")
public class PuntosApiServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        prepararRespuesta(response);

        String codigo =
                request.getParameter("codigo");

        try (PrintWriter out =
                     response.getWriter()) {

            if (codigo == null
                    || codigo.isBlank()) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST
                );

                out.print(
                        "{\"mensaje\":\"Debe ingresar el código del cliente.\"}"
                );

                return;
            }

            Cliente cliente =
                    new ClienteDAO()
                            .buscarPorCodigo(
                                    codigo.trim()
                                          .toUpperCase()
                            );

            if (cliente == null
                    || !cliente.isActivo()) {

                response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND
                );

                out.print(
                        "{\"mensaje\":\"Cliente no encontrado.\"}"
                );

                return;
            }

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            out.print(
                    "{"
                    + "\"codigo\":\""
                    + escaparJson(cliente.getCodigo())
                    + "\","
                    + "\"nombre\":\""
                    + escaparJson(cliente.getNombre())
                    + "\","
                    + "\"puntos\":"
                    + cliente.getSaldoPuntos()
                    + "}"
            );

        } catch (SQLException e) {

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().print(
                    "{\"mensaje\":\"No se pudo consultar el saldo.\"}"
            );
        }
    }

    @Override
    protected void doOptions(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        prepararRespuesta(response);

        response.setStatus(
                HttpServletResponse.SC_OK
        );
    }

    private void prepararRespuesta(
            HttpServletResponse response
    ) {

        response.setCharacterEncoding("UTF-8");

        response.setContentType(
                "application/json;charset=UTF-8"
        );

        response.setHeader(
                "Access-Control-Allow-Origin",
                "*"
        );

        response.setHeader(
                "Access-Control-Allow-Methods",
                "GET, OPTIONS"
        );

        response.setHeader(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );
    }

    private String escaparJson(String texto) {

        if (texto == null) {
            return "";
        }

        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}