package com.umg.sistemapedidosfidelizacion.controlador;

import com.lowagie.text.DocumentException;
import com.umg.sistemapedidosfidelizacion.servicio.ReportePDFServicio;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/reportes")
public class ReporteControlador {

    @GetMapping
    public String index() {
        return "reportes/index";
    }

    @GetMapping("/generar")
    public Object generar(
            @RequestParam String tipo,
            @RequestParam(
                    required = false,
                    defaultValue = ""
            ) String inicio,
            @RequestParam(
                    required = false,
                    defaultValue = ""
            ) String fin,
            Model model
    ) {

        try {
            ReportePDFServicio servicio =
                    new ReportePDFServicio();

            ByteArrayOutputStream salida =
                    new ByteArrayOutputStream();

            String nombreArchivo;

            switch (tipo) {

                case "ventas" -> {

                    if (inicio.isBlank()
                            || fin.isBlank()) {

                        throw new IllegalArgumentException(
                                "Debe seleccionar las dos fechas."
                        );
                    }

                    LocalDate fechaInicio =
                            LocalDate.parse(
                                    inicio
                            );

                    LocalDate fechaFin =
                            LocalDate.parse(
                                    fin
                            );

                    if (fechaFin.isBefore(
                            fechaInicio
                    )) {

                        throw new IllegalArgumentException(
                                "La fecha final no puede ser anterior a la fecha inicial."
                        );
                    }

                    servicio.ventasPorPeriodo(
                            fechaInicio,
                            fechaFin,
                            salida
                    );

                    nombreArchivo =
                            "ventas-periodo.pdf";
                }

                case "productos" -> {

                    servicio.productosMasVendidos(
                            salida
                    );

                    nombreArchivo =
                            "productos-mas-vendidos.pdf";
                }

                case "inventario" -> {

                    servicio.productosBajoInventario(
                            salida
                    );

                    nombreArchivo =
                            "inventario-bajo.pdf";
                }

                case "puntos" -> {

                    servicio.puntosPorCliente(
                            salida
                    );

                    nombreArchivo =
                            "puntos-clientes.pdf";
                }

                case "canjes" -> {

                    servicio.historialCanjes(
                            salida
                    );

                    nombreArchivo =
                            "historial-canjes.pdf";
                }

                case "menus" -> {

                    servicio.ventasMenus(
                            salida
                    );

                    nombreArchivo =
                            "ventas-menus.pdf";
                }

                default ->
                        throw new IllegalArgumentException(
                                "Tipo de reporte no válido."
                        );
            }

            return ResponseEntity
                    .ok()
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\""
                            + nombreArchivo
                            + "\""
                    )
                    .body(
                            salida.toByteArray()
                    );

        } catch (SQLException
                 | DocumentException
                 | IOException
                 | DateTimeParseException
                 | IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "reportes/index";
        }
    }
}
