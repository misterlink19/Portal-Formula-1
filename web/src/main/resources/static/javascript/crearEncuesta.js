// Función para validar el formulario
function validarFormulario() {
    var checkboxes = document.querySelectorAll('input[name="pilotosSeleccionados"]:checked');
    if (checkboxes.length < 5 || checkboxes.length > 10) {
        alert("Por favor, selecciona entre 5 y 10 pilotos.");
        return false;
    }

    var fechaInicio = document.getElementById("fechaInicio").value;
    var fechaLimite = document.getElementById("fechaLimite").value;
    var fechaInicioDate = new Date(fechaInicio);
    var fechaLimiteDate = new Date(fechaLimite);
    var fechaActual = new Date();

    if (fechaInicioDate < fechaActual) {
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
    var fechaActual = new Date();
    fechaActual.setHours(fechaActual.getHours() + 1);

    var fechaManana = new Date(fechaActual);
    fechaManana.setDate(fechaActual.getDate() + 1);

    var isoDateTimeActual = fechaActual.toISOString().substring(0, 16); // Recorta para mantener el formato yyyy-MM-ddTH:mm
    var isoDateTimeManana = fechaManana.toISOString().substring(0, 16); // Recorta para mantener el formato yyyy-MM-ddTH:mm

    document.getElementById("fechaInicio").value = isoDateTimeActual;
    document.getElementById("fechaLimite").value = isoDateTimeManana;
}

// Función para actualizar el contador de pilotos seleccionados
function actualizarContadorPilotos() {
    var checkboxes = document.querySelectorAll('input[name="pilotosSeleccionados"]:checked');
    document.getElementById("pilotosSeleccionadosCount").innerText = "Pilotos seleccionados: " + checkboxes.length;
}

// Función para reiniciar el contador de pilotos seleccionados
function reiniciarContadorPilotos() {
    document.getElementById("pilotosSeleccionadosCount").innerText = "Pilotos seleccionados: 0";
}

// Configurar Flatpickr para los campos de fecha y hora
document.addEventListener('DOMContentLoaded', function() {
    flatpickr("#fechaInicio", {
        enableTime: true,
        dateFormat: "Y-m-d\\TH:i",
        minDate: "today",
        time_24hr: true,
        defaultDate: new Date(new Date().setHours(new Date().getHours() + 1)).toISOString().substring(0, 16)
    });

    flatpickr("#fechaLimite", {
        enableTime: true,
        dateFormat: "Y-m-d\\TH:i",
        minDate: "today",
        time_24hr: true,
        defaultDate: new Date(new Date().setDate(new Date().getDate() + 1)).toISOString().substring(0, 16),
        defaultHour: 8,
        defaultMinute: 0
    });
});
