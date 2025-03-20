document.addEventListener("DOMContentLoaded", () => {
    const circuitoSelect = document.getElementById("circuito-select");
    const carSelect = document.getElementById("car-select");
    const consumoVueltaInput = document.getElementById("input-consumo-vuelta");
    const consumoTotalInput = document.getElementById("input-consumo-total");
    const fechaConsulta = document.getElementById("input-fecha-consulta");
    const resultSection = document.getElementById("result-section");
    const resultConsumoVuelta = document.getElementById("result-consumo-vuelta");
    const resultCircuito = document.getElementById("result-circuito");
    const resultCoche = document.getElementById("result-coche");
    const resultConsumoTotal = document.getElementById("result-consumo-total");
    const saveButton = document.getElementById("save-button");
    // Habilitar/deshabilitar el botón de asignar
    function updateButtonState() {
        saveButton.disabled = !(circuitoSelect.value && carSelect.value);
    }
    function calcularResultados(circuitoData, cocheData) {
        const { longitud, numVueltas } = circuitoData;
        const { consumo } = cocheData;

        const distanciaVueltaKm = longitud / 1000;
        const consumoPorVuelta = (distanciaVueltaKm / 100) * consumo;
        const consumoTotal = consumoPorVuelta * numVueltas;

        return {
            consumoPorVuelta: consumoPorVuelta.toFixed(2),
            consumoTotal: consumoTotal.toFixed(2),
        };
    }

    function obtenerDatosCircuito(option) {
        return {
            numVueltas: parseInt(option.dataset.numVueltas),
            longitud: parseFloat(option.dataset.longitud),
        };
    }

    function obtenerDatosCoche(option) {
        return {
            consumo: parseFloat(option.dataset.consumo),
        };
    }

    function actualizarResultados() {
        updateButtonState();
        const circuitoOption = circuitoSelect.selectedOptions[0];
        const cocheOption = carSelect.selectedOptions[0];

        if (circuitoOption.value && cocheOption.value) {
            const circuitoData = obtenerDatosCircuito(circuitoOption);
            const cocheData = obtenerDatosCoche(cocheOption);

            const resultados = calcularResultados(circuitoData, cocheData);

            // Actualizar la interfaz
            resultCircuito.textContent = circuitoOption.text;
            resultCoche.textContent = cocheOption.text;
            resultConsumoVuelta.textContent = `${resultados.consumoPorVuelta} litros`;
            resultConsumoTotal.textContent = `${resultados.consumoTotal} litros`;

            consumoVueltaInput.value = resultados.consumoPorVuelta;
            consumoTotalInput.value = resultados.consumoTotal;
            resultSection.classList.remove("hidden");
        }
    }

    circuitoSelect.addEventListener("change", actualizarResultados);
    carSelect.addEventListener("change", actualizarResultados);
});
