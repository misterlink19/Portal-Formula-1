<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.portal.formula1.model.Noticia" %>


<%
    List<Noticia> noticias = (List<Noticia>) request.getAttribute("noticias");

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title>Crear Noticia</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .form-container {
            background-color: rgba(255, 255, 255, 0.9);
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            padding: 20px 40px;
            width: 400px;
            text-align: center;
        }

        h1 {
            color: #333;
            margin-bottom: 20px;
        }

        label {
            font-weight: bold;
            display: block;
            margin: 15px 0 5px;
            text-align: left;
        }

        input[type="text"],
        textarea,
        input[type="file"] {
            width: 100%;
            padding: 10px;
            margin: 5px 0 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        button {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }

        button:hover {
            background-color: #0056b3;
        }

        .error {
            color: red;
            font-size: 14px;
            margin-top: -15px;
        }

        .message {
            color: green;
            font-size: 14px;
        }

    </style>
</head>
<body>
    <div class="form-container">
        <h1>Crear Noticia</h1>
        
        <c:if test="${not empty error}">
            <div class="error">
                <p>${error}</p>
            </div>
        </c:if>
        
        <form action="/admin/noticias/crear" method="post" enctype="multipart/form-data">
            <label for="titulo">Título:</label>
            <input type="text" id="titulo" name="titulo" value="${noticia.titulo}" required maxlength="100">
            <c:if test="${not empty fieldErrors.titulo}">
                <p class="error">${fieldErrors.titulo}</p>
            </c:if>
            
            <label for="descripcion">Descripción:</label>
            <textarea id="descripcion" name="descripcion" required minlength="500" maxlength="2000">${noticia.descripcion}</textarea>
            <c:if test="${not empty fieldErrors.descripcion}">
                <p class="error">${fieldErrors.descripcion}</p>
            </c:if>
            
            <label for="imagen">Imagen (opcional):</label>
            <input type="file" id="imagen" name="imagen" accept="image/jpeg, image/png">
            <c:if test="${not empty fieldErrors.imagen}">
                <p class="error">${fieldErrors.imagen}</p>
            </c:if>
            
            <button type="submit">Publicar</button>
        </form>
    </div>
</body>
</html>
