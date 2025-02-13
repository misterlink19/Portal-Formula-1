document.addEventListener('DOMContentLoaded', () => {
    const dialog = document.querySelector('.dialog');
    const deleteButtons = document.querySelectorAll('.button-accions-delete'); // Selecciona todos los botones
    const cancelButton = document.querySelector('.cancel-button');
    const confirmButton = document.querySelector('.confirm-button');

    let selectedCodigo = null; // Variable para almacenar el código del coche a eliminar

    // Agrega evento a cada botón de eliminar
    deleteButtons.forEach(button => {
        button.addEventListener('click', () => {
            selectedCodigo = button.getAttribute("data-codigo"); // Obtiene el código del coche seleccionado
            dialog.classList.remove('hidden'); // Muestra el diálogo de confirmación
        });
    });

    // Evento para cancelar
    cancelButton.addEventListener('click', () => {
        dialog.classList.add('hidden'); // Oculta el diálogo
        selectedCodigo = null; // Resetea el código seleccionado
    });

    // Evento para confirmar eliminación
    confirmButton.addEventListener('click', async () => {
        if (!selectedCodigo) return; // Evita errores si no hay código seleccionado

        try {
            const response = await fetch(`/coches/eliminar/${selectedCodigo}`, {
                method: 'DELETE',
            });

            if (response.ok) {
                window.location.href = "/coches/listar"; // Redirige a la lista de coches tras eliminar
            } else {
                const error = await response.text();
                alert("Error: " + error);
            }
        } catch (error) {
            alert("Ocurrió un error al intentar eliminar el coche: " + error.message);
        }

        dialog.classList.add('hidden'); // Oculta el diálogo después de eliminar
        selectedCodigo = null; // Resetea el código después de la acción
    });
});
