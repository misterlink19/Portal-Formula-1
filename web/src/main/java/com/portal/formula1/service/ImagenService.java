package com.portal.formula1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImagenService {

    // Ruta donde se almacenarán las imágenes (puedes cambiarla según sea necesario)
    private final Path rootLocation = Paths.get("uploads");

    // Tamaño máximo de la imagen en bytes (por ejemplo, 2 MB)
    private final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;

    // Tipos de imagen permitidos
    private final String[] ALLOWED_TYPES = {"image/jpeg", "image/png"};

    public ImagenService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento de imágenes", e);
        }
    }

    public boolean isFormatoValido(MultipartFile file) {
        String contentType = file.getContentType();
        for (String type : ALLOWED_TYPES) {
            if (type.equals(contentType)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTamanoValido(MultipartFile file) {
        return file.getSize() <= MAX_IMAGE_SIZE;
    }

    public String guardarImagen(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("La imagen está vacía");
        }
        if (!isFormatoValido(file)) {
            throw new IOException("Formato de imagen no permitido. Sólo se permiten JPG y PNG.");
        }
        if (!isTamanoValido(file)) {
            throw new IOException("La imagen supera el tamaño máximo permitido de 2 MB.");
        }

        // Generar un nombre de archivo único
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destinationFile = this.rootLocation.resolve(Paths.get(fileName))
                .normalize().toAbsolutePath();

        Files.copy(file.getInputStream(), destinationFile);
        return fileName; // Retorna el nombre de la imagen guardada
    }
}
