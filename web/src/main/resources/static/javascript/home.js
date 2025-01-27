document.addEventListener("DOMContentLoaded", function () {
    // Los eventos son inyectados por Thymeleaf como JSON
    const eventos = /*[[${eventos}]]*/ [];

    // Mapea los eventos para que sean compatibles con FullCalendar
    const eventosCalendario = eventos.map(evento => ({
        title: evento.nombreEvento,
        start: evento.fecha, // Fecha en formato ISO (YYYY-MM-DD)
        url: `/calendario/circuito/${evento.circuito.id}` // Enlace al detalle del circuito
    }));

    // Inicializa FullCalendar
    const calendarioEl = document.getElementById('calendario');
    const calendar = new FullCalendar.Calendar(calendarioEl, {
        initialView: 'dayGridMonth',
        locale: 'es', // Idioma español
        events: eventosCalendario // Carga los eventos al calendario
    });

    calendar.render();
});
