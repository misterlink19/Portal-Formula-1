document.addEventListener('DOMContentLoaded', () => {
    const dialog = document.querySelector('.dialog');
    const deleteButton = document.querySelector('.delete-button');
    const cancelButton = document.querySelector('.cancel-button');
    const confirmButton = document.querySelector('.confirm-button');
    const carSelect = document.getElementById("car-select");

    // Función para actualizar el estado del botón "Eliminar Coche"
    const updateDeleteButtonState = () => {
        const selectedOption = carSelect.options[carSelect.selectedIndex];

        // Deshabilitar el botón si no hay selección
        if (!selectedOption.value) {
            deleteButton.disabled = true;
            deleteButton.classList.add('hidden'); // Clase opcional para ocultarlo
        } else {
            deleteButton.disabled = false;
            deleteButton.classList.remove('hidden');
        }
    };

    // Evento para actualizar los detalles del coche y el estado del botón
    carSelect.addEventListener("change", function () {
        const selectedOption = this.options[this.selectedIndex];

        // Actualiza los detalles con los valores de los atributos data-*
        document.getElementById("detalle-nombre").textContent = selectedOption.getAttribute("data-nombre") || "";
        document.getElementById("detalle-ers-curva-lenta").textContent = selectedOption.getAttribute("data-ers-curva-lenta") || "";
        document.getElementById("detalle-ers-curva-media").textContent = selectedOption.getAttribute("data-ers-curva-media") || "";
        document.getElementById("detalle-ers-curva-rapida").textContent = selectedOption.getAttribute("data-ers-curva-rapida") || "";
        document.getElementById("detalle-consumo").textContent = selectedOption.getAttribute("data-consumo") || "";
        document.getElementById("detalle-equipo-id").textContent = selectedOption.getAttribute("data-equipo-id") || "";
        document.getElementById("detalle-piloto-id").textContent = selectedOption.getAttribute("data-piloto-id") || "Sin piloto asignado";

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
        const selectedOption = carSelect.options[carSelect.selectedIndex];
        const selectedCar = selectedOption.value;

        try {
            const response = await fetch(`/coches/eliminar/${selectedCar}`, {
                method: 'DELETE',
            });

            if (response.ok) {
                window.location.href = "/coches"; // Redirige a la lista de coches
            } else {
                const error = await response.text();
                alert("Error: " + error);
            }
        } catch (error) {
            alert("Ocurrió un error al intentar eliminar el coche: " + error.message);
        }

        dialog.classList.add('hidden');
    });
});
