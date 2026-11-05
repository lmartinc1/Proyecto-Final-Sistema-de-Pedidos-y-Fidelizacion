package com.umg.sistemapedidosfidelizacion.servicio;

import com.umg.sistemapedidosfidelizacion.dao.ClienteDAO;
import com.umg.sistemapedidosfidelizacion.dao.MenuDAO;
import com.umg.sistemapedidosfidelizacion.dao.PedidoDAO;
import com.umg.sistemapedidosfidelizacion.dao.ProductoDAO;
import com.umg.sistemapedidosfidelizacion.excepcion.InventarioInsuficienteException;
import com.umg.sistemapedidosfidelizacion.modelo.Cliente;
import com.umg.sistemapedidosfidelizacion.modelo.DetallePedido;
import com.umg.sistemapedidosfidelizacion.modelo.Menu;
import com.umg.sistemapedidosfidelizacion.modelo.Pedido;
import com.umg.sistemapedidosfidelizacion.modelo.Producto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoServicio {

    private final ClienteDAO clienteDAO;
    private final ProductoDAO productoDAO;
    private final MenuDAO menuDAO;
    private final PedidoDAO pedidoDAO;

    public PedidoServicio() {
        this.clienteDAO = new ClienteDAO();
        this.productoDAO = new ProductoDAO();
        this.menuDAO = new MenuDAO();
        this.pedidoDAO = new PedidoDAO();
    }

    public Pedido crearPedidoPendiente(
            String codigoCliente,
            List<DetallePedido> detalles
    ) throws SQLException, InventarioInsuficienteException {

        if (codigoCliente == null || codigoCliente.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe indicar el cliente."
            );
        }

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "El pedido debe contener al menos un producto o menú."
            );
        }

        Cliente cliente =
                clienteDAO.buscarPorCodigo(codigoCliente);

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

        List<DetallePedido> detallesValidos =
                prepararDetalles(detalles);

        validarDisponibilidad(detallesValidos);

        Pedido pedido = new Pedido();

        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setDetalles(detallesValidos);

        pedidoDAO.insertar(pedido);

        return pedido;
    }

    private List<DetallePedido> prepararDetalles(
            List<DetallePedido> detalles
    ) throws SQLException {

        List<DetallePedido> resultado =
                new ArrayList<>();

        for (DetallePedido detalle : detalles) {

            if (detalle == null) {
                throw new IllegalArgumentException(
                        "El pedido contiene un detalle inválido."
                );
            }

            if (detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor que cero."
                );
            }

            if ("PRODUCTO".equalsIgnoreCase(
                    detalle.getTipoItem()
            )) {

                if (detalle.getProducto() == null) {
                    throw new IllegalArgumentException(
                            "El producto del detalle es obligatorio."
                    );
                }

                Producto producto =
                        productoDAO.buscarPorId(
                                detalle.getProducto()
                                        .getIdProducto()
                        );

                if (producto == null) {
                    throw new IllegalArgumentException(
                            "El producto no existe."
                    );
                }

                if (!producto.isActivo()) {
                    throw new IllegalArgumentException(
                            "El producto "
                            + producto.getNombre()
                            + " está desactivado."
                    );
                }

                DetallePedido nuevoDetalle =
                        new DetallePedido(
                                "PRODUCTO",
                                producto,
                                null,
                                detalle.getCantidad(),
                                producto.getPrecio()
                        );

                resultado.add(nuevoDetalle);

            } else if ("MENU".equalsIgnoreCase(
                    detalle.getTipoItem()
            )) {

                if (detalle.getMenu() == null) {
                    throw new IllegalArgumentException(
                            "El menú del detalle es obligatorio."
                    );
                }

                Menu menu =
                        menuDAO.buscarPorId(
                                detalle.getMenu().getIdMenu()
                        );

                if (menu == null) {
                    throw new IllegalArgumentException(
                            "El menú no existe."
                    );
                }

                if (!menu.isActivo()) {
                    throw new IllegalArgumentException(
                            "El menú "
                            + menu.getNombre()
                            + " está desactivado."
                    );
                }

                DetallePedido nuevoDetalle =
                        new DetallePedido(
                                "MENU",
                                null,
                                menu,
                                detalle.getCantidad(),
                                menu.getPrecio()
                        );

                resultado.add(nuevoDetalle);

            } else {
                throw new IllegalArgumentException(
                        "Tipo de detalle no válido."
                );
            }
        }

        return resultado;
    }

    private void validarDisponibilidad(
            List<DetallePedido> detalles
    ) throws SQLException, InventarioInsuficienteException {

        Map<Integer, Integer> cantidades =
                new HashMap<>();

        for (DetallePedido detalle : detalles) {

            int cantidad = detalle.getCantidad();

            if ("PRODUCTO".equalsIgnoreCase(
                    detalle.getTipoItem()
            )) {

                agregarCantidad(
                        cantidades,
                        detalle.getProducto()
                                .getIdProducto(),
                        cantidad
                );

            } else {

                Menu menu = detalle.getMenu();

                agregarCantidad(
                        cantidades,
                        menu.getSandwich()
                                .getIdProducto(),
                        cantidad
                );

                agregarCantidad(
                        cantidades,
                        menu.getBebida()
                                .getIdProducto(),
                        cantidad
                );

                agregarCantidad(
                        cantidades,
                        menu.getAcompanamiento()
                                .getIdProducto(),
                        cantidad
                );
            }
        }

        for (Map.Entry<Integer, Integer> entrada
                : cantidades.entrySet()) {

            Producto producto =
                    productoDAO.buscarPorId(
                            entrada.getKey()
                    );

            int requerida = entrada.getValue();

            if (producto == null || !producto.isActivo()) {
                throw new InventarioInsuficienteException(
                        "Uno de los productos requeridos no está disponible."
                );
            }

            if (producto.getExistencia() < requerida) {
                throw new InventarioInsuficienteException(
                        "Inventario insuficiente para "
                        + producto.getNombre()
                        + ". Disponible: "
                        + producto.getExistencia()
                        + ". Requerido: "
                        + requerida
                        + "."
                );
            }
        }
    }

    private void agregarCantidad(
            Map<Integer, Integer> cantidades,
            int idProducto,
            int cantidad
    ) {

        cantidades.merge(
                idProducto,
                cantidad,
                Integer::sum
        );
    }

    public Pedido buscarPorId(int idPedido)
            throws SQLException {

        return pedidoDAO.buscarPorId(idPedido);
    }

    public List<Pedido> listar()
            throws SQLException {

        return pedidoDAO.listar();
    }

    public boolean anularPedido(int idPedido)
            throws SQLException {

        return pedidoDAO.anularPendiente(idPedido);
    }
}