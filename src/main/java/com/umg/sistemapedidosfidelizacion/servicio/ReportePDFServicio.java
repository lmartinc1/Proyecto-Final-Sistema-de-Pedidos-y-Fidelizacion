package com.umg.sistemapedidosfidelizacion.servicio;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.umg.sistemapedidosfidelizacion.dao.ReporteDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportePDFServicio {

    private final ReporteDAO reporteDAO;

    private final Font titulo;
    private final Font encabezado;
    private final Font texto;

    public ReportePDFServicio()
            throws DocumentException, IOException {

        this.reporteDAO =
                new ReporteDAO();

        BaseFont baseFont =
                BaseFont.createFont(
                        BaseFont.HELVETICA,
                        BaseFont.CP1252,
                        false
                );

        this.titulo =
                new Font(
                        baseFont,
                        16,
                        Font.BOLD
                );

        this.encabezado =
                new Font(
                        baseFont,
                        9,
                        Font.BOLD
                );

        this.texto =
                new Font(
                        baseFont,
                        9,
                        Font.NORMAL
                );
    }

    public void ventasPorPeriodo(
            LocalDate inicio,
            LocalDate fin,
            OutputStream salida
    ) throws Exception {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );

        crearPDF(
                "Ventas por período - "
                + inicio.format(formato)
                + " al "
                + fin.format(formato),

                new String[]{
                        "Fecha",
                        "Pedido",
                        "Código cliente",
                        "Cliente",
                        "Método",
                        "Monto"
                },

                reporteDAO.ventasPorPeriodo(
                        inicio,
                        fin
                ),

                salida,
                true
        );
    }

    public void productosMasVendidos(
            OutputStream salida
    ) throws Exception {

        crearPDF(
                "Productos más vendidos",

                new String[]{
                        "Código",
                        "Producto",
                        "Categoría",
                        "Unidades vendidas"
                },

                reporteDAO.productosMasVendidos(),

                salida,
                false
        );
    }

    public void productosBajoInventario(
            OutputStream salida
    ) throws Exception {

        crearPDF(
                "Productos agotados o con existencia menor o igual a 5",

                new String[]{
                        "Código",
                        "Categoría",
                        "Producto",
                        "Existencia",
                        "Estado"
                },

                reporteDAO.productosBajoInventario(),

                salida,
                false
        );
    }

    public void puntosPorCliente(
            OutputStream salida
    ) throws Exception {

        crearPDF(
                "Puntos acumulados por cliente",

                new String[]{
                        "Código",
                        "Cliente",
                        "Teléfono",
                        "Puntos",
                        "Estado"
                },

                reporteDAO.puntosPorCliente(),

                salida,
                false
        );
    }

    public void historialCanjes(
            OutputStream salida
    ) throws Exception {

        crearPDF(
                "Historial de canjes",

                new String[]{
                        "Fecha",
                        "Código cliente",
                        "Cliente",
                        "Recompensa",
                        "Puntos utilizados"
                },

                reporteDAO.historialCanjes(),

                salida,
                true
        );
    }

    public void ventasMenus(
            OutputStream salida
    ) throws Exception {

        crearPDF(
                "Ventas de menús completos",

                new String[]{
                        "Pedido",
                        "Fecha",
                        "Código cliente",
                        "Cliente",
                        "Código menú",
                        "Menú",
                        "Cantidad",
                        "Precio",
                        "Subtotal"
                },

                reporteDAO.ventasMenus(),

                salida,
                true
        );
    }

    private void crearPDF(
            String nombreReporte,
            String[] columnas,
            List<String[]> filas,
            OutputStream salida,
            boolean horizontal
    ) throws DocumentException {

        Document documento =
                new Document(
                        horizontal
                                ? PageSize.A4.rotate()
                                : PageSize.A4
                );

        PdfWriter.getInstance(
                documento,
                salida
        );

        documento.open();

        Paragraph encabezadoDocumento =
                new Paragraph(
                        "Pan, Puntos y Premios",
                        titulo
                );

        documento.add(encabezadoDocumento);

        Paragraph nombre =
                new Paragraph(
                        nombreReporte,
                        encabezado
                );

        nombre.setSpacingAfter(15);

        documento.add(nombre);

        PdfPTable tabla =
                new PdfPTable(
                        columnas.length
                );

        tabla.setWidthPercentage(100);

        for (String columna : columnas) {

            PdfPCell celda =
                    new PdfPCell(
                            new Phrase(
                                    columna,
                                    encabezado
                            )
                    );

            celda.setPadding(5);

            tabla.addCell(celda);
        }

        if (filas.isEmpty()) {

            PdfPCell celda =
                    new PdfPCell(
                            new Phrase(
                                    "No hay registros para mostrar.",
                                    texto
                            )
                    );

            celda.setColspan(
                    columnas.length
            );

            celda.setPadding(8);

            tabla.addCell(celda);

        } else {

            for (String[] fila : filas) {

                for (String valor : fila) {

                    PdfPCell celda =
                            new PdfPCell(
                                    new Phrase(
                                            valor == null
                                                    ? ""
                                                    : valor,
                                            texto
                                    )
                            );

                    celda.setPadding(4);

                    tabla.addCell(celda);
                }
            }
        }

        documento.add(tabla);

        documento.close();
    }
}