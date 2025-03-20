document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector("form");
    const nombre = document.getElementById("nombre");
    const apellidosInput = document.getElementById("apellidos");
    const dorsal = document.getElementById("dorsal");
    const twitter = document.getElementById("twitter");
    const pais = document.getElementById("pais");
    const siglasInput = document.getElementById("siglas");
    const fotoArchivo = document.getElementById("fotoArchivo");

        // Generar siglas automáticamente
        apellidosInput.addEventListener("input", function() {
            const apellidos = apellidosInput.value.trim();
            if (apellidos.length >= 3) {
                siglasInput.value = apellidos.substring(0, 3).toUpperCase();
            } else {
                siglasInput.value = "";
            }
        });

    form.addEventListener("submit", function(event) {
        let valid = true;

        document.querySelectorAll(".text-danger").forEach(e => e.textContent = "");

        if (nombre.value.trim() === "" || nombre.value.length > 125) {
            valid = false;
            document.querySelector("#nombre + .text-danger").textContent = "El nombre del piloto es obligatorio y no puede tener más de 125 caracteres.";
        }
        if (apellidos.value.trim() === "" || apellidos.value.length > 255) {
            valid = false;
            document.querySelector("#apellidos + .text-danger").textContent = "Los apellidos del piloto son obligatorios y no pueden tener más de 255 caracteres.";
        }
        if (dorsal.value.trim() === "" || dorsal.value.length > 3 || isNaN(dorsal.value) || parseInt(dorsal.value) <= 0) {
            valid = false;
            document.querySelector("#dorsal + .text-danger").textContent = "El dorsal del piloto es obligatorio, debe ser un número positivo y no puede tener más de 3 caracteres.";
        }
        if (twitter.value.length > 255) {
            valid = false;
            document.querySelector("#twitter + .text-danger").textContent = "El Twitter del piloto no puede tener más de 255 caracteres.";
        }
        if (pais.value.trim() === "" || pais.value.length > 255) {
            valid = false;
            document.querySelector("#pais + .text-danger").textContent = "El país del piloto es obligatorio y no puede tener más de 255 caracteres.";
        }
        if (!fotoArchivo.files[0] || !["image/jpeg", "image/png"].includes(fotoArchivo.files[0].type)) {
            valid = false;
            document.querySelector("#fotoArchivo + .text-danger").textContent = "La foto del piloto es obligatoria y debe ser una imagen JPG o PNG.";
        }

        if (!valid) {
            event.preventDefault();
        }
    });
});

function resetFormFields() {
    document.querySelector("form").reset();
    document.querySelectorAll(".text-danger").forEach(e => e.textContent = "");
}
