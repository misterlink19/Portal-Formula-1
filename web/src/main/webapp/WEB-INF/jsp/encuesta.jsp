<%-- 
    Document   : encuesta
    Created on : 6 nov 2024, 2:35:45 p. m.
    Author     : Misterlink
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Gestión de Encuestas</title>
        <style>
            .container {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                min-height: 100vh;
                background-color: #f0f0f0;
            }
            .button {
                margin: 10px;
                padding: 15px 25px;
                font-size: 16px;
                cursor: pointer;
                background-color: #007bff;
                color: white;
                border: none;
                border-radius: 5px;
                text-decoration: none;
            }
            .button:hover {
                background-color: #0056b3;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Gestión de Encuestas</h1>
            <a href="${pageContext.request.contextPath}/encuestas/crearEncuestas" class="button">Crear una Encuesta</a>
            <a href="${pageContext.request.contextPath}/encuestas/verEncuestas" class="button">Ver una Encuesta</a>
            <a href="${pageContext.request.contextPath}/menu" class="button">Salir al Menú Anterior</a>
        </div>
    </body>
</html>
