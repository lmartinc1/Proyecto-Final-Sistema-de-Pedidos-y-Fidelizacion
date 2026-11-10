package com.umg.sistemapedidosfidelizacion.api;

import com.umg.sistemapedidosfidelizacion.dao.ClienteDAO;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PuntosControlador {

    private final ClienteDAO clienteDAO =
            new ClienteDAO();

    @GetMapping("/puntos")
    public ResponseEntity<Map<String, Object>> consultar(
            @RequestParam(
                    required = false
            ) String codigo
    ) {

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        if (codigo == null
                || codigo.isBlank()) {

            respuesta.put(
                    "mensaje",
                    "Debe ingresar el código del cliente."
            );

            return ResponseEntity
                    .badRequest()
                    .body(
                            respuesta
                    );
        }

        try {
            Cliente cliente =
                    clienteDAO.buscarPorCodigo(
                            codigo.trim()
                                  .toUpperCase()
                    );

            if (cliente == null
                    || !cliente.isActivo()) {

                respuesta.put(
                        "mensaje",
                        "Cliente no encontrado."
                );

                return ResponseEntity
                        .status(
                                HttpStatus.NOT_FOUND
                        )
                        .body(
                                respuesta
                        );
            }

            respuesta.put(
                    "codigo",
                    cliente.getCodigo()
            );

            respuesta.put(
                    "nombre",
                    cliente.getNombre()
            );

            respuesta.put(
                    "puntos",
                    cliente.getSaldoPuntos()
            );

            return ResponseEntity
                    .ok(
                            respuesta
                    );

        } catch (SQLException e) {

            respuesta.put(
                    "mensaje",
                    "No se pudo consultar el saldo."
            );

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            respuesta
                    );
        }
    }
}
