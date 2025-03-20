function filtrarVotaciones() {
    console.log("PRUEBA DE TEST");
    const busqueda = document.getElementById('busqueda').value.toLowerCase();
    const estado = document.getElementById('estado').value;
    const fecha = document.getElementById('fecha').value;
    const mensajeCoincidencia = document.getElementById('no-coincidencias');


    const votaciones = Array.from(document.querySelectorAll('.votacion'));


    let hayCoincidencia = false;

    votaciones.forEach(votacion => {
        const titulo = votacion.getAttribute('data-titulo').toLowerCase();
        const descripcion = votacion.getAttribute('data-descripcion').toLowerCase();
        const fechaLimite = votacion.getAttribute('data-fecha-limite');
        const disponible = votacion.getAttribute('data-disponible') === 'true';


        const coincideBusqueda = (titulo.includes(busqueda) || descripcion.includes(busqueda));
        const coincideEstado = (estado === 'todos' || (estado === 'disponible' && disponible) || (estado === 'no disponible' && !disponible));
        const coincideFecha = (!fecha || fechaLimite >= fecha);


        if (coincideBusqueda && coincideEstado && coincideFecha) {
            votacion.style.display = '';
            hayCoincidencia = true;
        } else {
            votacion.style.display = 'none';
        }
    });

    if(hayCoincidencia){
        mensajeCoincidencia.style.display = 'none';
    }else{
        mensajeCoincidencia.style.display = ''
    }

}

document.getElementById('busqueda').addEventListener('input', filtrarVotaciones);
document.getElementById('estado').addEventListener('change', filtrarVotaciones);
document.getElementById('fecha').addEventListener('change', filtrarVotaciones);


filtrarVotaciones();