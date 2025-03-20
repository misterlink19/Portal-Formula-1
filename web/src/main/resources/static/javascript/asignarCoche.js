document.addEventListener("DOMContentLoaded", function() {
    const pilotSelect = document.getElementById("pilot-select");
    const carSelect = document.getElementById("car-select");
    const assignButton = document.getElementById("assign-button");

    // Habilitar/deshabilitar el botón de asignar
    function updateButtonState() {
        assignButton.disabled = !(pilotSelect.value && carSelect.value);
    }

    pilotSelect.addEventListener("change", updateButtonState);
    carSelect.addEventListener("change", updateButtonState);

    // Confirmación antes de enviar el formulario
    document.querySelector("form").addEventListener("submit", function(event) {
        const pilotId = pilotSelect.value;
        const carId = carSelect.value;

        if (!pilotId || !carId) {
            alert("Debe seleccionar un piloto y un coche.");
            event.preventDefault();
            return;
        }
    });
});