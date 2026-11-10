package com.umg.sistemapedidosfidelizacion.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InicioControlador {

    @GetMapping({"/", "/login"})
    public String inicio() {
        return "index";
    }

    @PostMapping("/login")
    public String ingresar(
            @RequestParam String usuario,
            @RequestParam String password,
            Model model
    ) {

        if (usuario == null
                || usuario.isBlank()
                || password == null
                || password.isBlank()) {

            model.addAttribute(
                    "error",
                    "Ingrese teléfono o correo y contraseña."
            );

            return "index";
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
