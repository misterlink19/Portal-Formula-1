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
    <title>Inicio de Sesi�n</title>
    <style>
        /* Estilos para centrar el panel en la pantalla */
        .login-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #f8f9fa;
        }
        .login-card {
            width: 25vw;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="card login-card">
            <div class="card-body">
                <h2 class="card-title text-center mb-4">Iniciar Sesi�n</h2>
                
                <!-- Formulario de inicio de sesi�n -->
                <form action="/" method="post" class="needs-validation" novalidate>
                    <!-- Campo de Correo Electr�nico (Usuario) -->
                    <div class="mb-3">
                        <label for="usuario" class="form-label">Correo Electr�nico</label>
                        <input type="email" class="form-control" id="usuario" name="usuario" required>
                        <div class="invalid-feedback">
                            Por favor ingrese un correo electr�nico v�lido.
                        </div>
                    </div>
                    
                    <!-- Campo de Contrase�a -->
                    <div class="mb-3">
                        <label for="contrasena" class="form-label">Contrase�a</label>
                        <input type="password" class="form-control" id="contrasena" name="contrasena" required minlength="5">
                        <div class="invalid-feedback">
                            Por favor ingrese su contrase�a (m�nimo 5 caracteres).
                        </div>
                    </div>
                    
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary">Inicio</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script>
        // Validaci�n de formulario de Bootstrap
        (function () {
            var forms = document.querySelectorAll('.needs-validation');
            Array.prototype.slice.call(forms).forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        })();
    </script>
</body>
</html>
