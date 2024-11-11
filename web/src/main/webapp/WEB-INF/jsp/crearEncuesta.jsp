<%-- 
    Document   : crearEncuesta
    Created on : 3 nov 2024, 8:12:02 p. m.
    Author     : Misterlink
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Crear Encuesta</title>
    </head>
    <body>
        <h1>Crear Nueva Encuesta</h1> 
        <form action="${pageContext.request.contextPath}/encuestas" method="post"> 
            <label for="titulo">Título:</label> 
            <input type="text" id="titulo" name="titulo" required> 

            <label for="descripcion">Descripción:</label> 
            <textarea id="descripcion" name="descripcion" required></textarea> 

            <label for="fechaLimite">Fecha Límite:</label> 
            <input type="datetime-local" id="fechaLimite" name="fechaLimite" required> 

            <h2>Selecciona Pilotos</h2>
            <ul> 
                <c:forEach var="piloto" items="${pilotos}"> 
                    <li> 
                        <input type="checkbox" name="pilotos" value="${piloto[0]}"> ${piloto[0]} ${piloto[1]} (${piloto[2]}) 
                    </li> 
                </c:forEach> 
            </ul>
            
            <button type="submit">Crear Encuesta</button>
        </form>
    </body>
</html>


