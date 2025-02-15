document.addEventListener("DOMContentLoaded", function () {
    if (typeof eventos === "undefined" || eventos.length === 0) {
        console.warn("No se encontraron eventos para el calendario.");
        return;
    }

    const calendarioEl = document.getElementById("calendario");
    const calendar = new FullCalendar.Calendar(calendarioEl, {
        initialView: "dayGridMonth",
        locale: "es",
        buttonText: {
            today: 'Hoy'
        },
        events: typeof eventos !== "undefined" && eventos.length > 0 ? eventos : [],
        height: 600,
        contentHeight: 570,
        eventClick: function (info) {
            if (info.event.url) {
                window.location.href = info.event.url;
                info.jsEvent.preventDefault();
            }
        },
    });

    calendar.render();
});