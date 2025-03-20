document.addEventListener('DOMContentLoaded', () => {
    const dialog = document.querySelector('.dialog');
    const deleteButton = document.querySelector('.delete-button');
    const cancelButton = document.querySelector('.cancel-button');
    const confirmButton = document.querySelector('.confirm-button');
    const userSelect = document.getElementById("user-select");
    const userEmail = document.getElementById("user-email").getAttribute("data-user-email");

    // Email del usuario actual (puedes obtenerlo desde el backend o configurarlo aquí)
    const currentUserEmail = userEmail; // Cambia esto al email del usuario logueado.

    // Función para actualizar el estado del botón "Eliminar Usuario"
    const updateDeleteButtonState = () => {
        const selectedOption = userSelect.options[userSelect.selectedIndex];
        const selectedEmail = selectedOption.getAttribute("data-email");

        // Deshabilitar el botón si no hay selección o si el email coincide con el usuario actual
        if (!selectedOption.value || selectedEmail === currentUserEmail) {
            deleteButton.disabled = true;
            deleteButton.classList.add('hidden'); // Clase opcional para estilos deshabilitados
        } else {
            deleteButton.disabled = false;
            deleteButton.classList.remove('hidden');
        }
    };

    // Evento para actualizar los detalles del usuario y el estado del botón
    userSelect.addEventListener("change", function () {
        const selectedOption = this.options[this.selectedIndex];

        // Actualiza los detalles con los valores de los atributos data-*
        document.getElementById("detalle-usuario").textContent = selectedOption.value || "";
        document.getElementById("detalle-nombre").textContent = selectedOption.getAttribute("data-nombre") || "";
        document.getElementById("detalle-email").textContent = selectedOption.getAttribute("data-email") || "";
        document.getElementById("detalle-rol").textContent = selectedOption.getAttribute("data-rol") || "";
        document.getElementById("detalle-fecha-registro").textContent = selectedOption.getAttribute("data-fecha-registro") || "";
        document.getElementById("detalle-validacion").textContent = selectedOption.getAttribute("data-validacion") || "";
        document.getElementById("detalle-equipo-id").textContent = selectedOption.getAttribute("data-equipo-id") || "";


        // Actualiza el estado del botón
        updateDeleteButtonState();
    });

    // Inicializa el estado del botón al cargar la página
    updateDeleteButtonState();

    // Eventos para manejar la funcionalidad del diálogo
    deleteButton.addEventListener('click', () => {
        dialog.classList.remove('hidden');
    });

    cancelButton.addEventListener('click', () => {
        dialog.classList.add('hidden');
    });

    confirmButton.addEventListener('click', async () => {
        const selectedOption = userSelect.options[userSelect.selectedIndex];
        const selectedUser = selectedOption.value;

        try {
            const response = await fetch(`/equipos/eliminar/${selectedUser}`, {
                method: 'DELETE',
            });

            if (response.ok) {
                alert("Usuario eliminado correctamente.");
                window.location.href = "/equipos"; // Redirige a /equipos sin recargar la página
            } else {
                const error = await response.text();
                alert("Error: " + error);
            }
        } catch (error) {
            alert("Ocurrió un error al intentar eliminar el usuario: " + error.message);
        }

        dialog.classList.add('hidden');
    });
});
