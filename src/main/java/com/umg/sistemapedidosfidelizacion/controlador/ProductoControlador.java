package com.umg.sistemapedidosfidelizacion.controlador;

import com.umg.sistemapedidosfidelizacion.dao.ProductoDAO;
import com.umg.sistemapedidosfidelizacion.modelo.Producto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

@Controller
@RequestMapping("/productos")
public class ProductoControlador {

    private static final Set<String> CATEGORIAS_VALIDAS =
            Set.of(
                    "SANDWICH",
                    "BEBIDA",
                    "ACOMPANAMIENTO"
            );

    private final ProductoDAO productoDAO =
            new ProductoDAO();

    @GetMapping
    public String listar(Model model) {

        try {
            model.addAttribute(
                    "productos",
                    productoDAO.listar()
            );

        } catch (SQLException e) {

            model.addAttribute(
                    "productos",
                    new ArrayList<>()
            );

            model.addAttribute(
                    "error",
                    "No se pudieron cargar los productos."
            );
        }

        return "productos/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        Producto producto =
                new Producto();

        producto.setCategoria("SANDWICH");
        producto.setExistencia(0);
        producto.setActivo(true);

        model.addAttribute(
                "producto",
                producto
        );

        return "productos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable int id,
            Model model
    ) {

        try {
            Producto producto =
                    productoDAO.buscarPorId(id);

            if (producto == null) {
                return "redirect:/productos";
            }

            model.addAttribute(
                    "producto",
                    producto
            );

            return "productos/formulario";

        } catch (SQLException e) {

            return "redirect:/productos";
        }
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("producto") Producto producto,
            Model model
    ) {

        try {
            validarProducto(
                    producto
            );

            if (producto.getIdProducto() <= 0) {

                producto.setCodigo(
                        producto.getCodigo()
                                .trim()
                                .toUpperCase()
                );

                if (productoDAO.existeCodigo(
                        producto.getCodigo()
                )) {

                    throw new IllegalArgumentException(
                            "El código del producto ya existe."
                    );
                }

                producto.setNombre(
                        producto.getNombre().trim()
                );

                producto.setExistencia(0);
                producto.setActivo(true);

                productoDAO.insertar(
                        producto
                );

            } else {

                Producto actual =
                        productoDAO.buscarPorId(
                                producto.getIdProducto()
                        );

                if (actual == null) {
                    throw new IllegalArgumentException(
                            "El producto no existe."
                    );
                }

                actual.setCategoria(
                        producto.getCategoria()
                );

                actual.setNombre(
                        producto.getNombre().trim()
                );

                actual.setPrecio(
                        producto.getPrecio()
                );

                productoDAO.actualizar(
                        actual
                );
            }

            return "redirect:/productos";

        } catch (SQLException
                 | IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "productos/formulario";
        }
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(
            @PathVariable int id,
            Model model
    ) {

        try {
            productoDAO.desactivar(id);

            return "redirect:/productos";

        } catch (SQLException e) {

            try {
                model.addAttribute(
                        "productos",
                        productoDAO.listar()
                );

            } catch (SQLException cargaError) {
                model.addAttribute(
                        "productos",
                        new ArrayList<>()
                );
            }

            model.addAttribute(
                    "error",
                    "No se pudo desactivar el producto."
            );

            return "productos/listar";
        }
    }

    private void validarProducto(
            Producto producto
    ) {

        if (producto.getCodigo() == null
                || producto.getCodigo().isBlank()
                || producto.getCategoria() == null
                || producto.getCategoria().isBlank()
                || producto.getNombre() == null
                || producto.getNombre().isBlank()
                || producto.getPrecio() == null) {

            throw new IllegalArgumentException(
                    "Todos los campos son obligatorios."
            );
        }

        if (!CATEGORIAS_VALIDAS.contains(
                producto.getCategoria()
        )) {

            throw new IllegalArgumentException(
                    "La categoría del producto no es válida."
            );
        }

        if (producto.getPrecio().compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "El precio no puede ser negativo."
            );
        }
    }
}
