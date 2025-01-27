document.addEventListener("DOMContentLoaded", function () {
    if (typeof eventos === "undefined" || eventos.length === 0) {
        console.warn("No se encontraron eventos para el calendario.");
        return;
    }

    const calendarioEl = document.getElementById("calendario");
    const calendar = new FullCalendar.Calendar(calendarioEl, {
        initialView: "dayGridMonth",
        locale: "es",
        events: eventos,
        height: 600, // Más alto para evitar scroll
        contentHeight: 570, // Ajustar el contenido interno
        eventClick: function (info) {
            if (info.event.url) {
                window.location.href = info.event.url;
                info.jsEvent.preventDefault();
            }
        },
    });

    calendar.render();
});
