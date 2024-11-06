<%-- 
    Document   : inicioSesion
    Created on : 30 oct 2024, 21:07:30
    Author     : fjavi
--%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%@ include file="header.jsp" %>
    <title>Acceso Denegado</title>
</head>
<body class="bg-light d-flex align-items-center justify-content-center" style="height: 100vh;">

    <div class="container text-center">
        <div class="card shadow-lg p-4">
            <div class="card-body">
                <h1 class="display-4 text-danger">Acceso Denegado</h1>
                <p class="lead text-muted mt-3">No tienes permiso para acceder a esta página.</p>
                <hr class="my-4">
                <p class="text-secondary">Si crees que esto es un error, contacta con el administrador del sistema.</p>
                <a href="/" class="btn btn-primary btn-lg mt-3">Volver</a>
            </div>
        </div>
    </div>

</body>
</html>
