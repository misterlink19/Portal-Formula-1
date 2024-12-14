// Función para validar el formulario
function validarFormulario() {
    var seleccionados = document.querySelectorAll('input[name="pilotosSeleccionados"]:checked');
    if (seleccionados.length < 5 || seleccionados.length > 10) {
        alert("Por favor, selecciona entre 5 y 10 pilotos.");
        return false;
    }

    var fechaInicio = document.getElementById("fechaInicio").value;
    var fechaLimite = document.getElementById("fechaLimite").value;
    var fechaInicioDate = new Date(fechaInicio);
    var fechaLimiteDate = new Date(fechaLimite);

    if (fechaInicioDate < new Date()) {
        alert("La fecha de inicio debe ser mayor o igual a la fecha y hora actual.");
        return false;
    }

    if (fechaLimiteDate < fechaInicioDate) {
        alert("La fecha límite debe ser mayor o igual a la fecha de inicio.");
        return false;
    }
    return true;
}

// Función para establecer las fechas iniciales
function setInitialDateTime() {
    const now = new Date();
    now.setHours(now.getHours() + 2); // Adelantar la hora actual en una hora

    const future = new Date();
    future.setDate(future.getDate() + 1); // Fecha límite adelantada en un dia

    const initialDateTime = now.toISOString().slice(0, 16); // Recorta para mantener el formato yyyy-MM-ddTH:mm
    const futureDateTime = future.toISOString().slice(0, 16); // Recorta para mantener el formato yyyy-MM-ddTH:mm

    document.getElementById("fechaInicio").value = initialDateTime;
    document.getElementById("fechaLimite").value = futureDateTime;
}
// Función para actualizar el contador de pilotos seleccionados
function actualizarContadorPilotos() {
    var checkboxes = document.querySelectorAll('input[name="pilotosSeleccionados"]:checked');
    document.getElementById("pilotosSeleccionadosCount").innerText = "Pilotos seleccionados: " + checkboxes.length;
}

// Función para reiniciar el contador de pilotos seleccionados
function reiniciarContadorPilotos() {
    document.getElementById("pilotosSeleccionadosCount").innerText = "Pilotos seleccionados: 0";
    setInitialDateTime();
}
document.addEventListener("DOMContentLoaded", function () {
    setInitialDateTime(); // Llamar a la función para establecer la fecha y hora iniciales

    flatpickr("#fechaInicio", {
        enableTime: true,
        dateFormat: "Y-m-d\\TH:i",
        minDate: "today",
        time_24hr: true,
        defaultDate: document.getElementById("fechaInicio").value,
    });

    flatpickr("#fechaLimite", {
        enableTime: true,
        dateFormat: "Y-m-d\\TH:i",
        minDate: "today",
        time_24hr: true,
        defaultDate: document.getElementById("fechaLimite").value,
    });

    document.querySelector('input[type="reset"]').addEventListener("click", function () {
        setTimeout(() => {
            document.getElementById("pilotosSeleccionadosCount").innerText = "Pilotos seleccionados: 0";
            setInitialDateTime();
        }, 0);
    });
});




