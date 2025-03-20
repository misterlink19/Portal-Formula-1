document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector("form");
    const nombreInput = document.getElementById("nombre");
    const logoInput = document.getElementById("logoArchivo");
    const twitterInput = document.getElementById("twitter");
    const errorMessages = {
        nombre: "El nombre del equipo es obligatorio y no puede tener más de 255 caracteres.",
        logo: "El logo del equipo es obligatorio y debe ser una imagen JPG o PNG.",
        twitter: "El Twitter del equipo no puede tener más de 255 caracteres."
    };

    form.addEventListener("submit", function(event) {
        let valid = true;

        // Limpiar mensajes de error
        document.querySelectorAll(".text-danger").forEach(el => el.textContent = "");

        // Validar el nombre del equipo
        if (nombreInput.value.trim() === "" || nombreInput.value.length > 255) {
            valid = false;
            document.querySelector("#nombre + .text-danger").textContent = errorMessages.nombre;
        }

        // Validar el logo del equipo
        const file = logoInput.files[0];
        if (!file) {
            valid = false;
            document.querySelector("#logoArchivo + .text-danger").textContent = errorMessages.logo;
        } else if (!["image/jpeg", "image/png"].includes(file.type)) {
            valid = false;
            document.querySelector("#logoArchivo + .text-danger").textContent = errorMessages.logo;
        }

        // Validar el Twitter del equipo
        if (twitterInput.value.length > 255) {
            valid = false;
            document.querySelector("#twitter + .text-danger").textContent = errorMessages.twitter;
        }

        if (!valid) {
            event.preventDefault();
        }
    });
});

function resetFormFields() {
    const form = document.querySelector("form");

    // Limpiar manualmente los campos de texto
    document.getElementById("nombre").value = "";
    document.getElementById("twitter").value = "";

    // Limpiar el campo del archivo
    document.getElementById("logoArchivo").value = "";

    // Limpiar mensajes de error
    document.querySelectorAll(".text-danger").forEach(el => el.textContent = "");
}
