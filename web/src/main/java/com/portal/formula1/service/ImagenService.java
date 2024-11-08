package com.portal.formula1.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagenService {

    @Value("${upload.dir}")
    private String uploadDir;

    public boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        long size = file.getSize();
        return (contentType.equals("image/jpeg") || contentType.equals("image/png")) && size <= 2 * 1024 * 1024;
    }

    public String guardarImagen(MultipartFile file) {
        try {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(uploadDir, filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, file.getBytes());
            // Devuelve solo el nombre del archivo para facilitar la integración con Thymeleaf
            return filename;
        } catch (IOException e) {
            throw new ImageStorageException("Error al guardar la imagen", e);
        }
    }
}

class ImageStorageException extends RuntimeException {
    public ImageStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
