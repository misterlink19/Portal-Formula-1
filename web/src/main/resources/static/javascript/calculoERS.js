document.addEventListener("DOMContentLoaded", () => {
    const circuitoSelect = document.getElementById("circuito-select");
    const carSelect = document.getElementById("car-select");

    // Inputs ocultos para enviar al backend
    const inputAhorradorUnitario = document.getElementById("input-ahorrador-unitario");
    const inputAhorradorTotal = document.getElementById("input-ahorrador-total");
    const inputNormalUnitario = document.getElementById("input-normal-unitario");
    const inputNormalTotal = document.getElementById("input-normal-total");
    const inputDeportivoUnitario = document.getElementById("input-deportivo-unitario");
    const inputDeportivoTotal = document.getElementById("input-deportivo-total");

    // Sección de resultados en la interfaz
    const resultSection = document.getElementById("result-section");
    const resultCircuito = document.getElementById("result-circuito");
    const resultCoche = document.getElementById("result-coche");

    const resultAhorradorUnitario = document.getElementById("result-ahorrador-unitario");
    const resultAhorradorTotal = document.getElementById("result-ahorrador-total");
    const resultNormalUnitario = document.getElementById("result-normal-unitario");
    const resultNormalTotal = document.getElementById("result-normal-total");
    const resultDeportivoUnitario = document.getElementById("result-deportivo-unitario");
    const resultDeportivoTotal = document.getElementById("result-deportivo-total");

    const saveButton = document.getElementById("save-button");

    function updateButtonState() {
        saveButton.disabled = !(circuitoSelect.value && carSelect.value);
    }

    function calcularERS(circuitoData, cocheData) {
        const {
            numeroCurvasLentas,
            numeroCurvasMedias,
            numeroCurvasRapidas,
        } = circuitoData;

        const {
            ersCurvaLenta,
            ersCurvaMedia,
            ersCurvaRapida,
        } = cocheData;

        // Fórmula base: Energía recuperada por vuelta
        const energiaBase = (numeroCurvasLentas * ersCurvaLenta) +
            (numeroCurvasMedias * ersCurvaMedia) +
            (numeroCurvasRapidas * ersCurvaRapida);

        // Aplicar modificadores según el modo de conducción
        const energiaAhorrador = Math.min(energiaBase * 1.05, 0.6);
        const energiaNormal = Math.min(energiaBase * 0.75, 0.6);
        const energiaDeportivo = Math.min(energiaBase * 0.40, 0.6);

        // Vueltas necesarias para cargar la batería (Capacidad = 1.2 kWh)
        const vueltasAhorrador = 1.2 / energiaAhorrador;
        const vueltasNormal = 1.2 / energiaNormal;
        const vueltasDeportivo = 1.2 / energiaDeportivo;

        return {
            ahorradorUnitario: energiaAhorrador.toFixed(2),
            ahorradorTotal: vueltasAhorrador.toFixed(2),
            normalUnitario: energiaNormal.toFixed(2),
            normalTotal: vueltasNormal.toFixed(2),
            deportivoUnitario: energiaDeportivo.toFixed(2),
            deportivoTotal: vueltasDeportivo.toFixed(2),
        };
    }

    function obtenerDatosCircuito(option) {
        return {
            numeroCurvasLentas: parseInt(option.dataset.numeroCurvasLentas),
            numeroCurvasMedias: parseInt(option.dataset.numeroCurvasMedia),
            numeroCurvasRapidas: parseInt(option.dataset.numeroCurvasRapida),
        };
    }

    function obtenerDatosCoche(option) {
        return {
            ersCurvaLenta: parseFloat(option.dataset.ersCurvaLenta),
            ersCurvaMedia: parseFloat(option.dataset.ersCurvaMedia),
            ersCurvaRapida: parseFloat(option.dataset.ersCurvaRapida),
        };
    }

    function actualizarResultados() {
        updateButtonState();
        const circuitoOption = circuitoSelect.selectedOptions[0];
        const cocheOption = carSelect.selectedOptions[0];

        if (circuitoOption.value && cocheOption.value) {
            const circuitoData = obtenerDatosCircuito(circuitoOption);
            const cocheData = obtenerDatosCoche(cocheOption);

            const resultados = calcularERS(circuitoData, cocheData);

            // Actualizar la interfaz
            resultCircuito.textContent = circuitoOption.text;
            resultCoche.textContent = cocheOption.text;

            resultAhorradorUnitario.textContent = `${resultados.ahorradorUnitario} kWh`;
            resultAhorradorTotal.textContent = `${resultados.ahorradorTotal} vueltas`;
            resultNormalUnitario.textContent = `${resultados.normalUnitario} kWh`;
            resultNormalTotal.textContent = `${resultados.normalTotal} vueltas`;
            resultDeportivoUnitario.textContent = `${resultados.deportivoUnitario} kWh`;
            resultDeportivoTotal.textContent = `${resultados.deportivoTotal} vueltas`;

            // Asignar valores a los inputs ocultos
            inputAhorradorUnitario.value = resultados.ahorradorUnitario;
            inputAhorradorTotal.value = resultados.ahorradorTotal;
            inputNormalUnitario.value = resultados.normalUnitario;
            inputNormalTotal.value = resultados.normalTotal;
            inputDeportivoUnitario.value = resultados.deportivoUnitario;
            inputDeportivoTotal.value = resultados.deportivoTotal;

            resultSection.classList.remove("hidden");
        }
    }

    circuitoSelect.addEventListener("change", actualizarResultados);
    carSelect.addEventListener("change", actualizarResultados);
});
