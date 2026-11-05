<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.Cliente" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    Cliente cliente =
            (Cliente) request.getAttribute("cliente");

    boolean editar =
            cliente != null;

    String codigo =
            editar
                    ? cliente.getCodigo()
                    : (String) request.getAttribute("codigoSiguiente");

    if (codigo == null) {
        codigo = request.getParameter("codigo");
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cliente</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-5">

    <div class="card shadow-sm border-0 mx-auto"
         style="max-width: 650px;">

        <div class="card-body p-4">

            <h1 class="h4 mb-4">
                <%= editar
                        ? "Editar cliente"
                        : "Nuevo cliente" %>
            </h1>

            <% if (request.getAttribute("error") != null) { %>

            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>

            <% } %>

            <form method="post"
                  action="<%= request.getContextPath() %>/clientes">

                <% if (editar) { %>

                <input type="hidden"
                       name="idCliente"
                       value="<%= cliente.getIdCliente() %>">

                <% } %>

                <div class="mb-3">

                    <label class="form-label">
                        Código
                    </label>

                    <input class="form-control"
                           name="codigo"
                           value="<%= codigo == null ? "" : codigo %>"
                           readonly>

                </div>

                <div class="mb-3">

                    <label class="form-label">
                        Nombre
                    </label>

                    <input class="form-control"
                           name="nombre"
                           required
                           value="<%= editar
                                   ? cliente.getNombre()
                                   : "" %>">

                </div>

                <div class="mb-4">

                    <label class="form-label">
                        Teléfono
                    </label>

                    <input class="form-control"
                           name="telefono"
                           value="<%= editar
                                   ? cliente.getTelefono()
                                   : "" %>">

                </div>

                <div class="d-flex gap-2">

                    <button class="btn btn-primary">
                        Guardar
                    </button>

                    <a class="btn btn-secondary"
                       href="<%= request.getContextPath() %>/clientes">
                        Cancelar
                    </a>

                </div>

            </form>

        </div>

    </div>

</div>

</body>
</html>