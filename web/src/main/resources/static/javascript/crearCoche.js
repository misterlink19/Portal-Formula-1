document.addEventListener("DOMContentLoaded", function () {
    // Obtener el formulario y los campos relevantes
    const carForm = document.getElementById("carForm");
    const ersCurvaLenta = document.getElementById("ers_curva_lenta");
    const ersCurvaMedia = document.getElementById("ers_curva_media");
    const ersCurvaRapida = document.getElementById("ers_curva_rapida");
    const consumo = document.getElementById("consumo");

    // Función para validar un campo
    function validateField(field, min, max) {
        const value = parseFloat(field.value);
        if (isNaN(value) || value < min || (max !== null && value > max)) {
            field.style.borderColor = "red";
            field.style.boxShadow = "0 0 0 2px rgba(255, 0, 0, 0.4)";
            return false;
        } else {
            field.style.borderColor = "#ddd";
            field.style.boxShadow = "none";
            return true;
        }
    }

    // Validar los campos al escribir
    [ersCurvaLenta, ersCurvaMedia, ersCurvaRapida].forEach(field => {
        field.addEventListener("input", () => {
            validateField(field, 0.01, 0.06);
        });
    });

    consumo.addEventListener("input", () => {
        validateField(consumo, 0, null); // Solo valida que sea mayor a 0
    });

    // Validar todos los campos al enviar el formulario
    carForm.addEventListener("submit", function (e) {
        const isValidLenta = validateField(ersCurvaLenta, 0.01, 0.06);
        const isValidMedia = validateField(ersCurvaMedia, 0.01, 0.06);
        const isValidRapida = validateField(ersCurvaRapida, 0.01, 0.06);
        const isValidConsumo = validateField(consumo, 0, null);

        // Si alguno de los campos no es válido, prevenir el envío
        if (!isValidLenta || !isValidMedia || !isValidRapida || !isValidConsumo) {
            e.preventDefault();
            alert("Por favor, corrige los campos marcados en rojo antes de enviar el formulario.");
        }
    });

    carForm.addEventListener("reset", function (e) {
        ersCurvaLenta.style.borderColor = "#ddd";
        ersCurvaLenta.style.boxShadow = "none";

        ersCurvaMedia.style.borderColor = "#ddd";
        ersCurvaMedia.style.boxShadow = "none";

        ersCurvaRapida.style.borderColor = "#ddd";
        ersCurvaRapida.style.boxShadow = "none";

        consumo.style.borderColor = "#ddd";
        consumo.style.boxShadow = "none";
    });
});