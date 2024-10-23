<%-- 
    Document   : aficionados
    Created on : 23 oct 2024, 21:31:53
    Author     : fjavi
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.portal.formula1.model.Aficionado" %>

<%
    // Supongamos que la lista de aficionados es pasada como un atributo de solicitud
    List<Aficionado> aficionados = (List<Aficionado>) request.getAttribute("aficionados");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Lista de Aficionados</title>
    <style>
        table {
            width: 50%;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 8px;
            text-align: left;
        }
    </style>
</head>
<body>
    <h1>Lista de Aficionados</h1>
    <table>
        <thead>
            <tr>
                <th>Nombre</th>
                <th>Edad</th>
            </tr>
        </thead>
        <tbody>
            <%
                if (aficionados != null && !aficionados.isEmpty()) {
                    for (Aficionado aficionado : aficionados) {
            %>
                        <tr>
                            <td><%= aficionado.getNombre() %></td>
                            <td><%= aficionado.getRol() %></td>
                        </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="2">No hay aficionados disponibles.</td>
                </tr>
            <%
                }
            %>
        </tbody>
    </table>
</body>
</html>

