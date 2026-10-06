package com.taller.auth.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WorkshopPhotoStorage {
    private static final long MAX_PHOTO_SIZE = 15L * 1024 * 1024;
    private final Path photoDirectory;

    public WorkshopPhotoStorage(@Value("${app.storage.workshop-photos-dir}") String photoDirectory) {
        this.photoDirectory = Path.of(photoDirectory).toAbsolutePath().normalize();
    }

    public StoredPhoto store(MultipartFile photo) {
        if (photo.isEmpty() || photo.getSize() > MAX_PHOTO_SIZE) {
            throw new IllegalArgumentException("La fotografia del taller es obligatoria y no puede superar 15 MB.");
        }
        String contentType = photo.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException("La fotografia del taller debe ser una imagen valida.");
        }

        String filename = UUID.randomUUID() + extensionOf(photo.getOriginalFilename());
        Path target = photoDirectory.resolve(filename).normalize();
        if (!target.startsWith(photoDirectory)) {
            throw new IllegalArgumentException("Nombre de archivo invalido.");
        }
        try {
            Files.createDirectories(photoDirectory);
            Files.copy(photo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return new StoredPhoto(filename, contentType);
        } catch (IOException exception) {
            throw new IllegalStateException("No fue posible guardar la fotografia del taller.", exception);
        }
    }

    public void deleteQuietly(String filename) {
        try {
            Files.deleteIfExists(photoDirectory.resolve(filename).normalize());
        } catch (IOException ignored) {
            // The database transaction is the source of truth; failed cleanup is non-blocking.
        }
    }

    private String extensionOf(String originalFilename) {
        if (originalFilename == null) return "";
        int dot = originalFilename.lastIndexOf('.');
        return dot >= 0 ? originalFilename.substring(dot).replaceAll("[^A-Za-z0-9.]", "") : "";
    }

    public record StoredPhoto(String filename, String contentType) {
    }
}
