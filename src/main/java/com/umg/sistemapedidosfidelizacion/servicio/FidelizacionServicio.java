package com.umg.sistemapedidosfidelizacion.servicio;

import com.umg.sistemapedidosfidelizacion.config.ConexionDB;
import com.umg.sistemapedidosfidelizacion.dao.ClienteDAO;
import com.umg.sistemapedidosfidelizacion.dao.FidelizacionDAO;
import com.umg.sistemapedidosfidelizacion.dao.InventarioDAO;
import com.umg.sistemapedidosfidelizacion.excepcion.InventarioInsuficienteException;
import com.umg.sistemapedidosfidelizacion.excepcion.PuntosInsuficientesException;
import com.umg.sistemapedidosfidelizacion.modelo.Canje;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;
import com.umg.sistemapedidosfidelizacion.modelo.Menu;
import com.umg.sistemapedidosfidelizacion.modelo.MovimientoPuntos;
import com.umg.sistemapedidosfidelizacion.modelo.Recompensa;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FidelizacionServicio {

    private final ClienteDAO clienteDAO;
    private final FidelizacionDAO fidelizacionDAO;
    private final InventarioDAO inventarioDAO;

    public FidelizacionServicio() {
        this.clienteDAO =
                new ClienteDAO();

        this.fidelizacionDAO =
                new FidelizacionDAO();

        this.inventarioDAO =
                new InventarioDAO();
    }

    public int consultarSaldo(
            String codigoCliente
    ) throws SQLException {

        return fidelizacionDAO
                .obtenerSaldoPorCodigo(
                        codigoCliente
                );
    }

    public List<Recompensa> listarRecompensas()
            throws SQLException {

        return fidelizacionDAO
                .listarRecompensasActivas();
    }

    public Canje canjear(
            String codigoCliente,
            String codigoRecompensa
    ) throws SQLException,
             InventarioInsuficienteException,
             PuntosInsuficientesException {

        Cliente cliente =
                clienteDAO.buscarPorCodigo(
                        codigoCliente
                );

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe."
            );
        }

        if (!cliente.isActivo()) {
            throw new IllegalArgumentException(
                    "El cliente está desactivado."
            );
        }

        Recompensa recompensa =
                fidelizacionDAO
                        .buscarRecompensaPorCodigo(
                                codigoRecompensa
                        );

        if (recompensa == null) {
            throw new IllegalArgumentException(
                    "La recompensa no existe."
            );
        }

        if (!recompensa.isActivo()) {
            throw new IllegalArgumentException(
                    "La recompensa está desactivada."
            );
        }

        try (Connection conexion =
                     ConexionDB.getConnection()) {

            boolean autoCommitOriginal =
                    conexion.getAutoCommit();

            conexion.setAutoCommit(false);

            try {
                int idCanje =
                        fidelizacionDAO.registrarCanje(
                                conexion,
                                cliente.getIdCliente(),
                                recompensa.getIdRecompensa(),
                                recompensa.getPuntosRequeridos()
                        );

                String referencia =
                        "Canje #" + idCanje;

                if ("PRODUCTO".equalsIgnoreCase(
                        recompensa.getTipoRecompensa()
                )) {

                    if (recompensa.getProducto() == null) {
                        throw new IllegalArgumentException(
                                "La recompensa no tiene producto asociado."
                        );
                    }

                    inventarioDAO.descontar(
                            conexion,
                            recompensa.getProducto()
                                    .getIdProducto(),
                            1,
                            "CANJE",
                            referencia
                    );

                } else if ("MENU".equalsIgnoreCase(
                        recompensa.getTipoRecompensa()
                )) {

                    Menu menu =
                            recompensa.getMenu();

                    if (menu == null) {
                        throw new IllegalArgumentException(
                                "La recompensa no tiene menú asociado."
                        );
                    }

                    inventarioDAO.descontar(
                            conexion,
                            menu.getSandwich()
                                    .getIdProducto(),
                            1,
                            "CANJE",
                            referencia
                    );

                    inventarioDAO.descontar(
                            conexion,
                            menu.getBebida()
                                    .getIdProducto(),
                            1,
                            "CANJE",
                            referencia
                    );

                    inventarioDAO.descontar(
                            conexion,
                            menu.getAcompanamiento()
                                    .getIdProducto(),
                            1,
                            "CANJE",
                            referencia
                    );

                } else {
                    throw new IllegalArgumentException(
                            "Tipo de recompensa no válido."
                    );
                }

                conexion.commit();

                cliente.setSaldoPuntos(
                        cliente.getSaldoPuntos()
                        - recompensa.getPuntosRequeridos()
                );

                return new Canje(
                        idCanje,
                        cliente,
                        recompensa,
                        LocalDateTime.now(),
                        recompensa.getPuntosRequeridos()
                );

            } catch (SQLException
                     | InventarioInsuficienteException
                     | PuntosInsuficientesException
                     | RuntimeException e) {

                conexion.rollback();
                throw e;

            } finally {

                conexion.setAutoCommit(
                        autoCommitOriginal
                );
            }
        }
    }

    public List<MovimientoPuntos> listarMovimientos(
            String codigoCliente
    ) throws SQLException {

        Cliente cliente =
                clienteDAO.buscarPorCodigo(
                        codigoCliente
                );

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe."
            );
        }

        return fidelizacionDAO
                .listarMovimientosPorCliente(
                        cliente.getIdCliente()
                );
    }

    public List<Canje> listarCanjes(
            String codigoCliente
    ) throws SQLException {

        Cliente cliente =
                clienteDAO.buscarPorCodigo(
                        codigoCliente
                );

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe."
            );
        }

        return fidelizacionDAO
                .listarCanjesPorCliente(
                        cliente.getIdCliente()
                );
    }
}