document.getElementById("crearNoticia").addEventListener("submit", function(event) {
    let isValid = true;

    // Validación del título
    const titulo = document.getElementById("titulo");
    const tituloError = document.querySelector('[th\\:errors="*{titulo}"]');
    if (titulo.value.trim() === "") {
        tituloError.textContent = "El título es obligatorio.";
        isValid = false;
    } else if (titulo.value.length > 100) {
        tituloError.textContent = "El título no debe superar los 100 caracteres.";
        isValid = false;
    } else {
        tituloError.textContent = "";
    }

    // Validación de la descripción
    const descripcion = document.getElementById("descripcion");
    const descripcionError = document.querySelector('[th\\:errors="*{texto}"]');
    if (descripcion.value.trim() === "") {
        descripcionError.textContent = "La descripción es obligatoria.";
        isValid = false;
    } else if (descripcion.value.length < 500) {
        descripcionError.textContent = "La descripción debe tener al menos 500 caracteres.";
        isValid = false;
    } else if (descripcion.value.length > 2000) {
        descripcionError.textContent = "La descripción no debe superar los 2000 caracteres.";
        isValid = false;
    } else {
        descripcionError.textContent = "";
    }

    // Validación de la imagen
    const imagen = document.getElementById("imagenArchivo");
    const imagenError = document.getElementById("imagenError");
    if (imagen.files.length > 0) {
        const file = imagen.files[0];
        const validExtensions = ["image/jpeg", "image/png"];
        if (!validExtensions.includes(file.type)) {
            imagenError.textContent = "Formato de imagen no válido. Solo JPG y PNG están permitidos.";
            isValid = false;
        } else if (file.size > 2 * 1024 * 1024) { // 2 MB
            imagenError.textContent = "El tamaño de la imagen no debe superar los 2 MB.";
            isValid = false;
        } else {
            imagenError.textContent = "";
        }
    } else {
        imagenError.textContent = "";
    }

    // Detener el envío si hay errores
    if (!isValid) {
        event.preventDefault();
    }
});
