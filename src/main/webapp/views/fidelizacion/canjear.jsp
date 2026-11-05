<%@ page import="java.util.List" %>
<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    List<Cliente> clientes =
            (List<Cliente>) request.getAttribute("clientes");

    List<Recompensa> recompensas =
            (List<Recompensa>) request.getAttribute("recompensas");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Fidelización</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <h1 class="h3">
        Fidelización
    </h1>

    <a href="<%= request.getContextPath() %>/dashboard.jsp">
        Volver al panel
    </a>

    <% if ("1".equals(request.getParameter("ok"))) { %>

    <div class="alert alert-success mt-3">
        Canje realizado correctamente.
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
                Realizar canje
            </h2>

            <form method="post"
                  action="<%= request.getContextPath() %>/fidelizacion">

                <div class="mb-3">

                    <label class="form-label">
                        Cliente
                    </label>

                    <select class="form-select"
                            name="codigoCliente">

                        <% if (clientes != null) {
                            for (Cliente cliente : clientes) { %>

                        <option value="<%= cliente.getCodigo() %>">
                            <%= cliente.getCodigo() %> -
                            <%= cliente.getNombre() %>
                            (<%= cliente.getSaldoPuntos() %> puntos)
                        </option>

                        <% }
                        } %>

                    </select>

                </div>

                <div class="mb-3">

                    <label class="form-label">
                        Recompensa
                    </label>

                    <select class="form-select"
                            name="codigoRecompensa">

                        <% if (recompensas != null) {
                            for (Recompensa recompensa : recompensas) { %>

                        <option value="<%= recompensa.getCodigo() %>">
                            <%= recompensa.getNombre() %>
                            -
                            <%= recompensa.getPuntosRequeridos() %> puntos
                        </option>

                        <% }
                        } %>

                    </select>

                </div>

                <button class="btn btn-primary">
                    Canjear
                </button>

            </form>

        </div>

    </div>

    <div class="card border-0 shadow-sm">

        <div class="card-body">

            <h2 class="h5">
                Consultar historial
            </h2>

            <% if (clientes != null) {
                for (Cliente cliente : clientes) { %>

            <a class="btn btn-outline-secondary btn-sm mb-2"
               href="<%= request.getContextPath() %>/fidelizacion?action=historial&codigo=<%= cliente.getCodigo() %>">

                <%= cliente.getCodigo() %> -
                <%= cliente.getNombre() %>

            </a>

            <% }
            } %>

        </div>

    </div>

</div>

</body>
</html>