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
    <title>Historial del cliente</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <h1 class="h3">
        Historial de <%= cliente.getNombre() %>
    </h1>

    <p>
        Código: <strong><%= cliente.getCodigo() %></strong>
        |
        Saldo actual:
        <strong><%= cliente.getSaldoPuntos() %> puntos</strong>
    </p>

    <a href="<%= request.getContextPath() %>/clientes"
       class="btn btn-secondary mb-4">
        Volver
    </a>

    <div class="card shadow-sm border-0 mb-4">

        <div class="card-body">

            <h2 class="h5">
                Movimientos de puntos
            </h2>

            <table class="table">

                <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Tipo</th>
                    <th>Puntos</th>
                    <th>Saldo</th>
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

    <div class="card shadow-sm border-0">

        <div class="card-body">

            <h2 class="h5">
                Canjes
            </h2>

            <table class="table">

                <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Recompensa</th>
                    <th>Puntos utilizados</th>
                </tr>
                </thead>

                <tbody>

                <% if (canjes != null) {
                    for (Canje canje : canjes) { %>

                <tr>
                    <td><%= canje.getFecha() %></td>
                    <td><%= canje.getRecompensa().getNombre() %></td>
                    <td><%= canje.getPuntosUtilizados() %></td>
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