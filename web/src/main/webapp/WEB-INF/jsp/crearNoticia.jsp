<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Crear Noticia</title>
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f0f2f5;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .form-container {
            background-color: #fff;
            border-radius: 15px;
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
            padding: 30px 40px;
            width: 100%;
            max-width: 500px;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .form-container:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
        }

        h1 {
            color: #333;
            margin-bottom: 20px;
            font-size: 24px;
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
            border: 1px solid #ddd;
            border-radius: 10px;
            font-size: 16px;
            background-color: #f9f9f9;
        }

        input[type="text"]:focus,
        textarea:focus,
        input[type="file"]:focus {
            border-color: #007bff;
            outline: none;
            background-color: #fff;
            box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
        }

        textarea {
            resize: none;
            height: 100px;
        }

        button {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.3s, transform 0.2s;
            width: 100%;
        }

        button:hover {
            background-color: #0056b3;
            transform: translateY(-2px);
        }

        .error {
            color: red;
            font-size: 14px;
            margin-top: -15px;
            margin-bottom: 10px;
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
        
        <%
            if (request.getAttribute("error") != null) {
        %>
            <div class="error">
                <p><%= request.getAttribute("error") %></p>
            </div>
        <%
            }
        %>
        
        <form action="<%= request.getContextPath() %>/admin/noticias/crear" method="post" enctype="multipart/form-data">
            <label for="titulo">Título:</label>
            <input type="text" id="titulo" name="titulo" value="<%= request.getAttribute("noticia") != null ? ((com.portal.formula1.model.Noticia) request.getAttribute("noticia")).getTitulo() : "" %>" required maxlength="100">
            <%
                if (request.getAttribute("fieldErrors") != null && ((java.util.Map<String, String>) request.getAttribute("fieldErrors")).containsKey("titulo")) {
            %>
                <p class="error"><%= ((java.util.Map<String, String>) request.getAttribute("fieldErrors")).get("titulo") %></p>
            <%
                }
            %>
            
            <label for="texto">Descripción:</label>
            <textarea id="texto" name="texto" required minlength="500" maxlength="2000"><%= request.getAttribute("noticia") != null ? ((com.portal.formula1.model.Noticia) request.getAttribute("noticia")).getTexto() : "" %></textarea>
            <%
                if (request.getAttribute("fieldErrors") != null && ((java.util.Map<String, String>) request.getAttribute("fieldErrors")).containsKey("texto")) {
            %>
                <p class="error"><%= ((java.util.Map<String, String>) request.getAttribute("fieldErrors")).get("texto") %></p>
            <%
                }
            %>
            
            <label for="imagen">Imagen (opcional):</label>
            <input type="file" id="imagen" name="imagen" accept="image/jpeg, image/png">
            <%
                if (request.getAttribute("fieldErrors") != null && ((java.util.Map<String, String>) request.getAttribute("fieldErrors")).containsKey("imagen")) {
            %>
                <p class="error"><%= ((java.util.Map<String, String>) request.getAttribute("fieldErrors")).get("imagen") %></p>
            <%
                }
            %>
            
            <button type="submit">Publicar</button>
        </form>
    </div>
</body>
</html>
