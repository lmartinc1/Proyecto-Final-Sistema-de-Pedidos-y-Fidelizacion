<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Reportes</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-4">

    <div class="mb-4">

        <h1 class="h3">
            Reportes PDF
        </h1>

        <a href="<%= request.getContextPath() %>/dashboard.jsp">
            Volver al panel
        </a>

    </div>

    <% if (request.getAttribute("error") != null) { %>

    <div class="alert alert-danger">
        <%= request.getAttribute("error") %>
    </div>

    <% } %>


    <div class="card border-0 shadow-sm mb-4">

        <div class="card-body">

            <h2 class="h5">
                Ventas por período
            </h2>

            <form method="get"
                  action="<%= request.getContextPath() %>/reportes"
                  target="_blank"
                  class="row g-3">

                <input type="hidden"
                       name="tipo"
                       value="ventas">

                <div class="col-md-4">

                    <label class="form-label">
                        Fecha inicial
                    </label>

                    <input class="form-control"
                           type="date"
                           name="inicio"
                           required>

                </div>

                <div class="col-md-4">

                    <label class="form-label">
                        Fecha final
                    </label>

                    <input class="form-control"
                           type="date"
                           name="fin"
                           required>

                </div>

                <div class="col-md-4 d-flex align-items-end">

                    <button class="btn btn-primary">
                        Generar PDF
                    </button>

                </div>

            </form>

        </div>

    </div>


    <div class="row g-4">

        <div class="col-md-6">

            <div class="card h-100 border-0 shadow-sm">

                <div class="card-body">

                    <h2 class="h5">
                        Productos más vendidos
                    </h2>

                    <p class="text-muted">
                        Incluye los componentes utilizados dentro de menús completos.
                    </p>

                    <a class="btn btn-primary"
                       target="_blank"
                       href="<%= request.getContextPath() %>/reportes?tipo=productos">
                        Generar PDF
                    </a>

                </div>

            </div>

        </div>


        <div class="col-md-6">

            <div class="card h-100 border-0 shadow-sm">

                <div class="card-body">

                    <h2 class="h5">
                        Productos agotados o con existencia ≤ 5
                    </h2>

                    <a class="btn btn-primary"
                       target="_blank"
                       href="<%= request.getContextPath() %>/reportes?tipo=inventario">
                        Generar PDF
                    </a>

                </div>

            </div>

        </div>


        <div class="col-md-6">

            <div class="card h-100 border-0 shadow-sm">

                <div class="card-body">

                    <h2 class="h5">
                        Puntos acumulados por cliente
                    </h2>

                    <a class="btn btn-primary"
                       target="_blank"
                       href="<%= request.getContextPath() %>/reportes?tipo=puntos">
                        Generar PDF
                    </a>

                </div>

            </div>

        </div>


        <div class="col-md-6">

            <div class="card h-100 border-0 shadow-sm">

                <div class="card-body">

                    <h2 class="h5">
                        Historial de canjes
                    </h2>

                    <a class="btn btn-primary"
                       target="_blank"
                       href="<%= request.getContextPath() %>/reportes?tipo=canjes">
                        Generar PDF
                    </a>

                </div>

            </div>

        </div>


        <div class="col-md-6">

            <div class="card h-100 border-0 shadow-sm">

                <div class="card-body">

                    <h2 class="h5">
                        Ventas de menús completos
                    </h2>

                    <a class="btn btn-primary"
                       target="_blank"
                       href="<%= request.getContextPath() %>/reportes?tipo=menus">
                        Generar PDF
                    </a>

                </div>

            </div>

        </div>

    </div>

</div>

</body>
</html>