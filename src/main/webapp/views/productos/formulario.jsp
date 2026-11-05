<%@ page import="com.umg.sistemapedidosfidelizacion.modelo.Producto" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    Producto producto =
            (Producto) request.getAttribute("producto");

    boolean editar =
            producto != null;
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Producto</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-5">

    <div class="card shadow-sm border-0 mx-auto"
         style="max-width: 650px;">

        <div class="card-body">

            <h1 class="h4 mb-4">
                <%= editar
                        ? "Editar producto"
                        : "Nuevo producto" %>
            </h1>

            <% if (request.getAttribute("error") != null) { %>

            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>

            <% } %>

            <form method="post"
                  action="<%= request.getContextPath() %>/productos">

                <% if (editar) { %>

                <input type="hidden"
                       name="idProducto"
                       value="<%= producto.getIdProducto() %>">

                <% } %>

                <div class="mb-3">

                    <label class="form-label">
                        Código
                    </label>

                    <input class="form-control"
                           name="codigo"
                           required
                           <%= editar ? "readonly" : "" %>
                           value="<%= editar ? producto.getCodigo() : "" %>">

                </div>

                <div class="mb-3">

                    <label class="form-label">
                        Categoría
                    </label>

                    <select class="form-select"
                            name="categoria">

                        <option value="SANDWICH"
                                <%= editar && "SANDWICH".equals(producto.getCategoria())
                                        ? "selected"
                                        : "" %>>
                            Sándwich
                        </option>

                        <option value="BEBIDA"
                                <%= editar && "BEBIDA".equals(producto.getCategoria())
                                        ? "selected"
                                        : "" %>>
                            Bebida
                        </option>

                        <option value="ACOMPANAMIENTO"
                                <%= editar && "ACOMPANAMIENTO".equals(producto.getCategoria())
                                        ? "selected"
                                        : "" %>>
                            Acompañamiento
                        </option>

                    </select>

                </div>

                <div class="mb-3">

                    <label class="form-label">
                        Nombre
                    </label>

                    <input class="form-control"
                           name="nombre"
                           required
                           value="<%= editar ? producto.getNombre() : "" %>">

                </div>

                <div class="mb-4">

                    <label class="form-label">
                        Precio
                    </label>

                    <input class="form-control"
                           type="number"
                           step="0.01"
                           min="0"
                           name="precio"
                           required
                           value="<%= editar ? producto.getPrecio() : "" %>">

                </div>

                <p class="text-muted">
                    La existencia se modifica desde el módulo Inventario.
                </p>

                <button class="btn btn-primary">
                    Guardar
                </button>

                <a class="btn btn-secondary"
                   href="<%= request.getContextPath() %>/productos">
                    Cancelar
                </a>

            </form>

        </div>

    </div>

</div>

</body>
</html>