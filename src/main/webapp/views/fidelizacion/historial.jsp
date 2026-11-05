<%@ page import="java.util.List" %>
<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    Cliente cliente =
            (Cliente) request.getAttribute("cliente");

    List<MovimientoPuntos> movimientos =
            (List<MovimientoPuntos>) request.getAttribute("movimientos");

    List<Canje> canjes =
            (List<Canje>) request.getAttribute("canjes");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Historial de fidelización</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <h1 class="h3">
        Fidelización de <%= cliente.getNombre() %>
    </h1>

    <p>
        Saldo:
        <strong><%= cliente.getSaldoPuntos() %> puntos</strong>
    </p>

    <a class="btn btn-secondary mb-4"
       href="<%= request.getContextPath() %>/fidelizacion">
        Volver
    </a>

    <div class="card border-0 shadow-sm mb-4">

        <div class="card-body">

            <h2 class="h5">
                Movimientos
            </h2>

            <table class="table">

                <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Tipo</th>
                    <th>Puntos</th>
                    <th>Saldo resultante</th>
                </tr>
                </thead>

                <tbody>

                <% if (movimientos != null) {
                    for (MovimientoPuntos m : movimientos) { %>

                <tr>
                    <td><%= m.getFecha() %></td>
                    <td><%= m.getTipo() %></td>
                    <td><%= m.getPuntos() %></td>
                    <td><%= m.getSaldoResultante() %></td>
                </tr>

                <% }
                } %>

                </tbody>

            </table>

        </div>

    </div>

    <div class="card border-0 shadow-sm">

        <div class="card-body">

            <h2 class="h5">
                Canjes
            </h2>

            <table class="table">

                <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Recompensa</th>
                    <th>Puntos</th>
                </tr>
                </thead>

                <tbody>

                <% if (canjes != null) {
                    for (Canje c : canjes) { %>

                <tr>
                    <td><%= c.getFecha() %></td>
                    <td><%= c.getRecompensa().getNombre() %></td>
                    <td><%= c.getPuntosUtilizados() %></td>
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