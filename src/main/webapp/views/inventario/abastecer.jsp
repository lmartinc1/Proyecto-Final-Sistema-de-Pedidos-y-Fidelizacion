<%@ page import="java.util.List" %>
<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    List<Producto> productos =
            (List<Producto>) request.getAttribute("productos");

    List<MovimientoInventario> movimientos =
            (List<MovimientoInventario>) request.getAttribute("movimientos");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Inventario</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <h1 class="h3">Inventario</h1>

    <a href="<%= request.getContextPath() %>/dashboard.jsp">
        Volver al panel
    </a>

    <% if ("1".equals(request.getParameter("ok"))) { %>

    <div class="alert alert-success mt-3">
        Inventario actualizado correctamente.
    </div>

    <% } %>

    <% if (request.getAttribute("error") != null) { %>

    <div class="alert alert-danger mt-3">
        <%= request.getAttribute("error") %>
    </div>

    <% } %>

    <div class="card border-0 shadow-sm my-4">

        <div class="card-body">

            <h2 class="h5">
                Abastecer producto
            </h2>

            <form method="post"
                  action="<%= request.getContextPath() %>/inventario"
                  class="row g-3">

                <div class="col-md-5">

                    <label class="form-label">
                        Producto
                    </label>

                    <select class="form-select"
                            name="idProducto"
                            required>

                        <% if (productos != null) {
                            for (Producto p : productos) { %>

                        <option value="<%= p.getIdProducto() %>">
                            <%= p.getCodigo() %> -
                            <%= p.getNombre() %>
                            (Existencia: <%= p.getExistencia() %>)
                        </option>

                        <% }
                        } %>

                    </select>

                </div>

                <div class="col-md-3">

                    <label class="form-label">
                        Cantidad
                    </label>

                    <input class="form-control"
                           type="number"
                           min="1"
                           name="cantidad"
                           required>

                </div>

                <div class="col-md-4">

                    <label class="form-label">
                        Referencia
                    </label>

                    <input class="form-control"
                           name="referencia"
                           placeholder="Ej. Compra semanal">

                </div>

                <div class="col-12">

                    <button class="btn btn-primary">
                        Registrar abastecimiento
                    </button>

                </div>

            </form>

        </div>

    </div>

    <div class="card border-0 shadow-sm">

        <div class="card-body">

            <h2 class="h5">
                Historial de movimientos
            </h2>

            <table class="table">

                <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Producto</th>
                    <th>Tipo</th>
                    <th>Cantidad</th>
                    <th>Referencia</th>
                </tr>
                </thead>

                <tbody>

                <% if (movimientos != null) {
                    for (MovimientoInventario m : movimientos) { %>

                <tr>
                    <td><%= m.getFecha() %></td>
                    <td><%= m.getProducto().getNombre() %></td>
                    <td><%= m.getTipo() %></td>
                    <td><%= m.getCantidad() %></td>
                    <td><%= m.getReferencia() == null ? "" : m.getReferencia() %></td>
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