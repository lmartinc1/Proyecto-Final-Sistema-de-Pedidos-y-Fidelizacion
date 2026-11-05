<%@ page import="java.util.List" %>
<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.Pedido" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    List<Pedido> pedidos =
            (List<Pedido>) request.getAttribute("pedidos");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Pedidos</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <div class="d-flex justify-content-between mb-4">

        <div>
            <h1 class="h3">Pedidos</h1>

            <a href="<%= request.getContextPath() %>/dashboard.jsp">
                Volver al panel
            </a>
        </div>

        <a class="btn btn-primary"
           href="<%= request.getContextPath() %>/pedidos?action=nuevo">
            Nuevo pedido
        </a>

    </div>

    <div class="card border-0 shadow-sm">

        <div class="card-body">

            <table class="table table-hover">

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Fecha</th>
                    <th>Cliente</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>

                <% if (pedidos != null) {
                    for (Pedido pedido : pedidos) { %>

                <tr>

                    <td>#<%= pedido.getIdPedido() %></td>
                    <td><%= pedido.getFecha() %></td>
                    <td><%= pedido.getCliente().getNombre() %></td>
                    <td>Q<%= pedido.getTotal() %></td>
                    <td><%= pedido.getEstado() %></td>

                    <td class="d-flex gap-2 flex-wrap">

                        <a class="btn btn-sm btn-outline-primary"
                           href="<%= request.getContextPath() %>/pedidos?action=detalle&id=<%= pedido.getIdPedido() %>">
                            Ver
                        </a>

                        <% if ("PENDIENTE".equals(pedido.getEstado())) { %>

                        <a class="btn btn-sm btn-success"
                           href="<%= request.getContextPath() %>/pagos?idPedido=<%= pedido.getIdPedido() %>">
                            Cobrar
                        </a>

                        <form method="post"
                              action="<%= request.getContextPath() %>/pedidos">

                            <input type="hidden"
                                   name="action"
                                   value="anular">

                            <input type="hidden"
                                   name="id"
                                   value="<%= pedido.getIdPedido() %>">

                            <button class="btn btn-sm btn-outline-danger">
                                Anular
                            </button>

                        </form>

                        <% } %>

                        <% if ("PAGADO".equals(pedido.getEstado())) { %>

                        <form method="post"
                              action="<%= request.getContextPath() %>/pedidos">

                            <input type="hidden"
                                   name="action"
                                   value="entregar">

                            <input type="hidden"
                                   name="id"
                                   value="<%= pedido.getIdPedido() %>">

                            <button class="btn btn-sm btn-success">
                                Marcar entregado
                            </button>

                        </form>

                        <% } %>

                    </td>

                </tr>

                <% }
                } %>

                </tbody>

            </table>

        </div>

    </div>

</div>

</body>
</html>