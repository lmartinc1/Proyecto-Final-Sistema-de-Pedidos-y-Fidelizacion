<%@ page import="java.util.List" %>
<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.Producto" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    List<Producto> productos =
            (List<Producto>) request.getAttribute("productos");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Productos</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <div class="d-flex justify-content-between mb-4">

        <div>
            <h1 class="h3">Productos</h1>

            <a href="<%= request.getContextPath() %>/dashboard.jsp">
                Volver al panel
            </a>
        </div>

        <a class="btn btn-primary"
           href="<%= request.getContextPath() %>/productos?action=nuevo">
            Nuevo producto
        </a>

    </div>

    <div class="card shadow-sm border-0">

        <div class="card-body">

            <table class="table table-hover">

                <thead>
                <tr>
                    <th>Código</th>
                    <th>Categoría</th>
                    <th>Nombre</th>
                    <th>Precio</th>
                    <th>Existencia</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>

                <% if (productos != null) {
                    for (Producto producto : productos) { %>

                <tr>

                    <td><%= producto.getCodigo() %></td>
                    <td><%= producto.getCategoria() %></td>
                    <td><%= producto.getNombre() %></td>
                    <td>Q<%= producto.getPrecio() %></td>
                    <td><%= producto.getExistencia() %></td>

                    <td>
                        <%= producto.isActivo()
                                ? "Activo"
                                : "Inactivo" %>
                    </td>

                    <td class="d-flex gap-2">

                        <a class="btn btn-sm btn-outline-primary"
                           href="<%= request.getContextPath() %>/productos?action=editar&id=<%= producto.getIdProducto() %>">
                            Editar
                        </a>

                        <% if (producto.isActivo()) { %>

                        <form method="post"
                              action="<%= request.getContextPath() %>/productos">

                            <input type="hidden"
                                   name="action"
                                   value="desactivar">

                            <input type="hidden"
                                   name="id"
                                   value="<%= producto.getIdProducto() %>">

                            <button class="btn btn-sm btn-outline-danger">
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

</body>
</html>