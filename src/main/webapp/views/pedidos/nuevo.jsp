<%@ page import="java.util.List" %>
<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    List<Cliente> clientes =
            (List<Cliente>) request.getAttribute("clientes");

    List<Producto> productos =
            (List<Producto>) request.getAttribute("productos");

    List<Menu> menus =
            (List<Menu>) request.getAttribute("menus");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo pedido</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <h1 class="h3 mb-4">
        Nuevo pedido
    </h1>

    <% if (request.getAttribute("error") != null) { %>

    <div class="alert alert-danger">
        <%= request.getAttribute("error") %>
    </div>

    <% } %>

    <form method="post"
          action="<%= request.getContextPath() %>/pedidos">

        <div class="card border-0 shadow-sm mb-4">

            <div class="card-body">

                <label class="form-label">
                    Cliente
                </label>

                <select class="form-select"
                        name="codigoCliente"
                        required>

                    <% if (clientes != null) {
                        for (Cliente cliente : clientes) { %>

                    <option value="<%= cliente.getCodigo() %>">
                        <%= cliente.getCodigo() %> -
                        <%= cliente.getNombre() %>
                    </option>

                    <% }
                    } %>

                </select>

            </div>

        </div>

        <div class="card border-0 shadow-sm mb-4">

            <div class="card-body">

                <h2 class="h5">
                    Productos
                </h2>

                <table class="table">

                    <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Precio</th>
                        <th>Disponible</th>
                        <th style="width: 140px;">Cantidad</th>
                    </tr>
                    </thead>

                    <tbody>

                    <% if (productos != null) {
                        for (Producto p : productos) { %>

                    <tr>

                        <td>
                            <%= p.getCodigo() %> -
                            <%= p.getNombre() %>
                        </td>

                        <td>Q<%= p.getPrecio() %></td>

                        <td><%= p.getExistencia() %></td>

                        <td>

                            <input type="number"
                                   min="0"
                                   value="0"
                                   class="form-control"
                                   name="producto_<%= p.getIdProducto() %>">

                        </td>

                    </tr>

                    <% }
                    } %>

                    </tbody>

                </table>

            </div>

        </div>

        <div class="card border-0 shadow-sm mb-4">

            <div class="card-body">

                <h2 class="h5">
                    Menús completos
                </h2>

                <table class="table">

                    <thead>
                    <tr>
                        <th>Menú</th>
                        <th>Componentes</th>
                        <th>Precio</th>
                        <th style="width: 140px;">Cantidad</th>
                    </tr>
                    </thead>

                    <tbody>

                    <% if (menus != null) {
                        for (Menu menu : menus) { %>

                    <tr>

                        <td>
                            <%= menu.getCodigo() %> -
                            <%= menu.getNombre() %>
                        </td>

                        <td>
                            <%= menu.getSandwich().getNombre() %>,
                            <%= menu.getBebida().getNombre() %>,
                            <%= menu.getAcompanamiento().getNombre() %>
                        </td>

                        <td>
                            Q<%= menu.getPrecio() %>
                        </td>

                        <td>

                            <input type="number"
                                   min="0"
                                   value="0"
                                   class="form-control"
                                   name="menu_<%= menu.getIdMenu() %>">

                        </td>

                    </tr>

                    <% }
                    } %>

                    </tbody>

                </table>

            </div>

        </div>

        <button class="btn btn-primary">
            Crear pedido
        </button>

        <a class="btn btn-secondary"
           href="<%= request.getContextPath() %>/pedidos">
            Cancelar
        </a>

    </form>

</div>

</body>
</html>