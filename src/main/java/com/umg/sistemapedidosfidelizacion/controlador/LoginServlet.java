package com.umg.sistemapedidosfidelizacion.controlador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String usuario =
                request.getParameter("usuario");

        String password =
                request.getParameter("password");

        if (usuario == null
                || usuario.isBlank()
                || password == null
                || password.isBlank()) {

            request.setAttribute(
                    "error",
                    "Ingrese teléfono o correo y contraseña."
            );

            request.getRequestDispatcher(
                    "/index.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/dashboard.jsp"
        );
    }
}