<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Panel principal</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/assets/css/estilos.css">
</head>

<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">

    <div class="container">

        <span class="navbar-brand fw-bold">
            Pan, Puntos y Premios
        </span>

        <a href="<%= request.getContextPath() %>/index.jsp"
           class="btn btn-outline-light btn-sm">
            Salir
        </a>

    </div>

</nav>

<main class="container py-5">

    <div class="mb-4">

        <h1 class="h3 fw-bold">
            Panel principal
        </h1>

        <p class="text-muted">
            Seleccione un módulo del sistema.
        </p>

    </div>

    <div class="row g-4">

        <div class="col-md-6 col-lg-4">

            <div class="card h-100 shadow-sm border-0">

                <div class="card-body">

                    <h2 class="h5">
                        Clientes
                    </h2>

                    <p class="text-muted">
                        Registro, búsqueda, modificación e historial.
                    </p>

                    <a href="<%= request.getContextPath() %>/clientes"
                       class="btn btn-primary">
                        Abrir
                    </a>

                </div>

            </div>

        </div>


        <div class="col-md-6 col-lg-4">

            <div class="card h-100 shadow-sm border-0">

                <div class="card-body">

                    <h2 class="h5">
                        Productos
                    </h2>

                    <p class="text-muted">
                        Catálogo de sándwiches, bebidas y acompañamientos.
                    </p>

                    <a href="<%= request.getContextPath() %>/productos"
                       class="btn btn-primary">
                        Abrir
                    </a>

                </div>

            </div>

        </div>


        <div class="col-md-6 col-lg-4">

            <div class="card h-100 shadow-sm border-0">

                <div class="card-body">

                    <h2 class="h5">
                        Inventario
                    </h2>

                    <p class="text-muted">
                        Abastecimiento e historial de movimientos.
                    </p>

                    <a href="<%= request.getContextPath() %>/inventario"
                       class="btn btn-primary">
                        Abrir
                    </a>

                </div>

            </div>

        </div>


        <div class="col-md-6 col-lg-4">

            <div class="card h-100 shadow-sm border-0">

                <div class="card-body">

                    <h2 class="h5">
                        Pedidos
                    </h2>

                    <p class="text-muted">
                        Pedidos pendientes, pagados y entregados.
                    </p>

                    <a href="<%= request.getContextPath() %>/pedidos"
                       class="btn btn-primary">
                        Abrir
                    </a>

                </div>

            </div>

        </div>


        <div class="col-md-6 col-lg-4">

            <div class="card h-100 shadow-sm border-0">

                <div class="card-body">

                    <h2 class="h5">
                        Fidelización
                    </h2>

                    <p class="text-muted">
                        Puntos, recompensas y canjes.
                    </p>

                    <a href="<%= request.getContextPath() %>/fidelizacion"
                       class="btn btn-primary">
                        Abrir
                    </a>

                </div>

            </div>

        </div>


        <div class="col-md-6 col-lg-4">

            <div class="card h-100 shadow-sm border-0">

                <div class="card-body">

                    <h2 class="h5">
                        Reportes
                    </h2>

                    <p class="text-muted">
                        Reportes de ventas, productos y fidelización.
                    </p>

                    <a href="<%= request.getContextPath() %>/reportes"
                       class="btn btn-primary">
                        Abrir
                    </a>

                </div>

            </div>

        </div>

    </div>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>