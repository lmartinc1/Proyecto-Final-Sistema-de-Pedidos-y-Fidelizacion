package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.servicio.ReportePDFServicio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/reportes")
public class ReporteServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String tipo =
                request.getParameter("tipo");

        if (tipo == null
                || tipo.isBlank()) {

            request.getRequestDispatcher(
                    "/views/reportes/index.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }

        try {
            ReportePDFServicio servicio =
                    new ReportePDFServicio();

            response.setContentType(
                    "application/pdf"
            );

            response.setHeader(
                    "Content-Disposition",
                    "inline; filename=\"reporte.pdf\""
            );

            switch (tipo) {

                case "ventas" -> {

                    String inicioTexto =
                            request.getParameter("inicio");

                    String finTexto =
                            request.getParameter("fin");

                    if (inicioTexto == null
                            || inicioTexto.isBlank()
                            || finTexto == null
                            || finTexto.isBlank()) {

                        throw new IllegalArgumentException(
                                "Debe seleccionar las dos fechas."
                        );
                    }

                    LocalDate inicio =
                            LocalDate.parse(
                                    inicioTexto
                            );

                    LocalDate fin =
                            LocalDate.parse(
                                    finTexto
                            );

                    if (fin.isBefore(inicio)) {

                        throw new IllegalArgumentException(
                                "La fecha final no puede ser anterior a la fecha inicial."
                        );
                    }

                    servicio.ventasPorPeriodo(
                            inicio,
                            fin,
                            response.getOutputStream()
                    );
                }

                case "productos" ->
                        servicio.productosMasVendidos(
                                response.getOutputStream()
                        );

                case "inventario" ->
                        servicio.productosBajoInventario(
                                response.getOutputStream()
                        );

                case "puntos" ->
                        servicio.puntosPorCliente(
                                response.getOutputStream()
                        );

                case "canjes" ->
                        servicio.historialCanjes(
                                response.getOutputStream()
                        );

                case "menus" ->
                        servicio.ventasMenus(
                                response.getOutputStream()
                        );

                default ->
                        throw new IllegalArgumentException(
                                "Tipo de reporte no válido."
                        );
            }

        } catch (Exception e) {

            if (!response.isCommitted()) {

                request.setAttribute(
                        "error",
                        e.getMessage()
                );

                request.getRequestDispatcher(
                        "/views/reportes/index.jsp"
                ).forward(
                        request,
                        response
                );

            } else {

                throw new ServletException(
                        "No se pudo generar el PDF.",
                        e
                );
            }
        }
    }
}