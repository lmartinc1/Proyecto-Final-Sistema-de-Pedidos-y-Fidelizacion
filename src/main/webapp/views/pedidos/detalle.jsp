<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    Pedido pedido =
            (Pedido) request.getAttribute("pedido");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle del pedido</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <% if (pedido != null) { %>

    <h1 class="h3">
        Pedido #<%= pedido.getIdPedido() %>
    </h1>

    <p>
        Cliente:
        <strong><%= pedido.getCliente().getNombre() %></strong>
    </p>

    <p>
        Estado:
        <strong><%= pedido.getEstado() %></strong>
    </p>

    <div class="card border-0 shadow-sm mb-4">

        <div class="card-body">

            <table class="table">

                <thead>
                <tr>
                    <th>Tipo</th>
                    <th>Artículo</th>
                    <th>Cantidad</th>
                    <th>Precio</th>
                    <th>Subtotal</th>
                </tr>
                </thead>

                <tbody>

                <% for (DetallePedido detalle : pedido.getDetalles()) {

                    String nombre =
                            "PRODUCTO".equals(detalle.getTipoItem())
                                    ? detalle.getProducto().getNombre()
                                    : detalle.getMenu().getNombre();
                %>

                <tr>
                    <td><%= detalle.getTipoItem() %></td>
                    <td><%= nombre %></td>
                    <td><%= detalle.getCantidad() %></td>
                    <td>Q<%= detalle.getPrecioUnitario() %></td>
                    <td>Q<%= detalle.getSubtotal() %></td>
                </tr>

                <% } %>

                </tbody>

            </table>

            <h2 class="h4 text-end">
                Total: Q<%= pedido.getTotal() %>
            </h2>

        </div>

    </div>

    <% if ("PENDIENTE".equals(pedido.getEstado())) { %>

    <a class="btn btn-success"
       href="<%= request.getContextPath() %>/pagos?idPedido=<%= pedido.getIdPedido() %>">
        Cobrar pedido
    </a>

    <% } %>

    <% } %>

    <a class="btn btn-secondary"
       href="<%= request.getContextPath() %>/pedidos">
        Volver
    </a>

</div>

</body>
</html>