<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    MetodoPago pago =
            (MetodoPago) request.getAttribute("pago");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Comprobante</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-5">

    <div class="card shadow-sm border-0 mx-auto"
         style="max-width: 650px;">

        <div class="card-body">

            <h1 class="h4 text-success">
                Pago aprobado
            </h1>

            <hr>

            <p>
                Pedido:
                <strong>#<%= pago.getPedido().getIdPedido() %></strong>
            </p>

            <p>
                Cliente:
                <strong><%= pago.getPedido().getCliente().getNombre() %></strong>
            </p>

            <p>
                Total:
                <strong>Q<%= pago.getMonto() %></strong>
            </p>

            <% if (pago instanceof PagoEfectivo) {
                PagoEfectivo efectivo =
                        (PagoEfectivo) pago;
            %>

            <p>
                Recibido:
                Q<%= efectivo.getMontoRecibido() %>
            </p>

            <p>
                Cambio:
                Q<%= efectivo.getCambio() %>
            </p>

            <% } else { %>

            <p>
                Referencia:
                <%= pago.getReferencia() %>
            </p>

            <% } %>

            <p>
                Puntos actuales:
                <strong>
                    <%= pago.getPedido().getCliente().getSaldoPuntos() %>
                </strong>
            </p>

            <a class="btn btn-primary"
               href="<%= request.getContextPath() %>/pedidos">
                Volver a pedidos
            </a>

        </div>

    </div>

</div>

</body>
</html>