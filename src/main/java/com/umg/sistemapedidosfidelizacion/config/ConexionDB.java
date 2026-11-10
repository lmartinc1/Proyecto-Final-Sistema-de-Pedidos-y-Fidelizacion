package com.umg.sistemapedidosfidelizacion.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {

    private static final String ARCHIVO_CONFIGURACION = "/db.properties";
    private static final String DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";

    private ConexionDB() {
    }

    public static Connection getConnection() throws SQLException {

        cargarDriver();

        Properties propiedades = new Properties();

        try (InputStream entrada =
                     ConexionDB.class.getResourceAsStream(
                             ARCHIVO_CONFIGURACION
                     )) {

            if (entrada == null) {
                throw new SQLException(
                        "No se encontró el archivo db.properties."
                );
            }

            propiedades.load(
                    new InputStreamReader(
                            entrada,
                            StandardCharsets.UTF_8
                    )
            );

        } catch (IOException e) {

            throw new SQLException(
                    "No se pudo leer la configuración de la base de datos.",
                    e
            );
        }

        String url =
                propiedades.getProperty("db.url");

        String usuario =
                propiedades.getProperty("db.usuario");

        String password =
                propiedades.getProperty("db.password");

        if (url == null
                || usuario == null
                || password == null) {

            throw new SQLException(
                    "La configuración de la base de datos está incompleta."
            );
        }

        return DriverManager.getConnection(
                url,
                usuario,
                password
        );
    }

    private static void cargarDriver()
            throws SQLException {

        try {
            Class.forName(DRIVER_MYSQL);

        } catch (ClassNotFoundException e) {

            throw new SQLException(
                    "No se encontró el driver JDBC de MySQL.",
                    e
            );
        }
    }
}