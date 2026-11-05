<%@ page import="java.util.List" %>
<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.Cliente" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    List<Cliente> clientes =
            (List<Cliente>) request.getAttribute("clientes");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clientes</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3">Clientes</h1>
            <a href="<%= request.getContextPath() %>/dashboard.jsp">
                Volver al panel
            </a>
        </div>

        <a class="btn btn-primary"
           href="<%= request.getContextPath() %>/clientes?action=nuevo">
            Nuevo cliente
        </a>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body">

            <div class="table-responsive">

                <table class="table table-hover">

                    <thead>
                    <tr>
                        <th>Código</th>
                        <th>Nombre</th>
                        <th>Teléfono</th>
                        <th>Puntos</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>

                    <tbody>

                    <% if (clientes != null) {
                        for (Cliente cliente : clientes) { %>

                    <tr>

                        <td><%= cliente.getCodigo() %></td>
                        <td><%= cliente.getNombre() %></td>
                        <td><%= cliente.getTelefono() %></td>
                        <td><%= cliente.getSaldoPuntos() %></td>

                        <td>
                            <%= cliente.isActivo()
                                    ? "Activo"
                                    : "Inactivo" %>
                        </td>

                        <td class="d-flex gap-2 flex-wrap">

                            <a class="btn btn-sm btn-outline-primary"
                               href="<%= request.getContextPath() %>/clientes?action=editar&id=<%= cliente.getIdCliente() %>">
                                Editar
                            </a>

                            <a class="btn btn-sm btn-outline-secondary"
                               href="<%= request.getContextPath() %>/clientes?action=historial&id=<%= cliente.getIdCliente() %>">
                                Historial
                            </a>

                            <% if (cliente.isActivo()) { %>

                            <form method="post"
                                  action="<%= request.getContextPath() %>/clientes">

                                <input type="hidden"
                                       name="action"
                                       value="desactivar">

                                <input type="hidden"
                                       name="id"
                                       value="<%= cliente.getIdCliente() %>">

                                <button class="btn btn-sm btn-outline-danger"
                                        type="submit">
                                    Desactivar
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

</div>

</body>
</html>