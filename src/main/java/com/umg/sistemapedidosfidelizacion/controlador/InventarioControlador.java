package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.dao.InventarioDAO;
import com.umg.sistemapedidosfidelizacion.dao.ProductoDAO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;

@Controller
@RequestMapping("/inventario")
public class InventarioControlador {

    private final InventarioDAO inventarioDAO =
            new InventarioDAO();

    private final ProductoDAO productoDAO =
            new ProductoDAO();

    @GetMapping
    public String mostrar(
            @RequestParam(
                    name = "ok",
                    required = false
            ) String ok,
            Model model
    ) {

        cargarDatos(
                model
        );

        model.addAttribute(
                "ok",
                "1".equals(ok)
        );

        return "inventario/abastecer";
    }

    @PostMapping("/abastecer")
    public String abastecer(
            @RequestParam String idProducto,
            @RequestParam String cantidad,
            @RequestParam(
                    required = false,
                    defaultValue = ""
            ) String referencia,
            Model model
    ) {

        try {
            int id =
                    Integer.parseInt(
                            idProducto
                    );

            int unidades =
                    Integer.parseInt(
                            cantidad
                    );

            inventarioDAO.abastecer(
                    id,
                    unidades,
                    referencia
            );

            return "redirect:/inventario?ok=1";

        } catch (NumberFormatException e) {

            model.addAttribute(
                    "error",
                    "Producto o cantidad no válidos."
            );

        } catch (SQLException
                 | IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );
        }

        cargarDatos(
                model
        );

        return "inventario/abastecer";
    }

    private void cargarDatos(
            Model model
    ) {

        try {
            model.addAttribute(
                    "productos",
                    productoDAO.listarActivos()
            );

            model.addAttribute(
                    "movimientos",
                    inventarioDAO.listarMovimientos()
            );

        } catch (SQLException e) {

            model.addAttribute(
                    "productos",
                    new ArrayList<>()
            );

            model.addAttribute(
                    "movimientos",
                    new ArrayList<>()
            );

            if (!model.containsAttribute("error")) {
                model.addAttribute(
                        "error",
                        "No se pudo cargar el inventario."
                );
            }
        }
    }
}
