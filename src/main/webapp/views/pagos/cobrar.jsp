<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.Pedido" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    Pedido pedido =
            (Pedido) request.getAttribute("pedido");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cobrar pedido</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-5">

    <div class="card shadow-sm border-0 mx-auto"
         style="max-width: 700px;">

        <div class="card-body">

            <h1 class="h4">
                Cobrar pedido #<%= pedido == null ? "" : pedido.getIdPedido() %>
            </h1>

            <% if (request.getAttribute("error") != null) { %>

            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>

            <% } %>

            <% if (pedido != null) { %>

            <p>
                Cliente:
                <strong><%= pedido.getCliente().getNombre() %></strong>
            </p>

            <p class="h5">
                Total: Q<%= pedido.getTotal() %>
            </p>

            <form method="post"
                  action="<%= request.getContextPath() %>/pagos">

                <input type="hidden"
                       name="idPedido"
                       value="<%= pedido.getIdPedido() %>">

                <div class="mb-3">

                    <label class="form-label">
                        Método de pago
                    </label>

                    <select class="form-select"
                            name="metodo"
                            id="metodo"
                            onchange="cambiarMetodo()">

                        <option value="EFECTIVO">
                            Efectivo
                        </option>

                        <option value="TARJETA">
                            Tarjeta
                        </option>

                    </select>

                </div>

                <div id="efectivo"
                     class="mb-3">

                    <label class="form-label">
                        Monto recibido
                    </label>

                    <input class="form-control"
                           type="number"
                           step="0.01"
                           min="0"
                           name="montoRecibido">

                </div>

                <div id="tarjeta"
                     class="mb-3 d-none">

                    <label class="form-label">
                        Resultado simulado
                    </label>

                    <select class="form-select"
                            name="resultadoTarjeta">

                        <option value="APROBADO">
                            Aprobado
                        </option>

                        <option value="RECHAZADO">
                            Rechazado
                        </option>

                    </select>

                </div>

                <button class="btn btn-success">
                    Procesar pago
                </button>

                <a class="btn btn-secondary"
                   href="<%= request.getContextPath() %>/pedidos">
                    Cancelar
                </a>

            </form>

            <% } %>

        </div>

    </div>

</div>

<script>
function cambiarMetodo() {
    const metodo = document.getElementById("metodo").value;
    document.getElementById("efectivo").classList.toggle("d-none", metodo !== "EFECTIVO");
    document.getElementById("tarjeta").classList.toggle("d-none", metodo !== "TARJETA");
}
</script>

</body>
</html>