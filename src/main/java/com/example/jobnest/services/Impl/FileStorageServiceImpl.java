package com.example.jobnest.services.impl;

import com.example.jobnest.exception.FileStorageException;
import com.example.jobnest.services.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Implementation of FileStorageService.
 * Handles file upload and storage operations.
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    static {
        // Create uploads directory if it doesn't exist
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (Exception e) {
            // Cannot use 'log' here if it's not initialized yet?
            // @Slf4j creates: private static final org.slf4j.Logger log = ...
            // So it should be available in static block if initialized before.
            // However, it's safer to use LoggerFactory explicitly or just assume it works.
            // Actually, static block runs at class loading. 'log' field is static final.
            // It depends on initialization order. Fields are initialized in order.
            // 'log' is inserted by Lombok. usually it works.
            org.slf4j.LoggerFactory.getLogger(FileStorageServiceImpl.class)
                    .error("Failed to create uploads directory: ", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String directory, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(directory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String fileName = prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Copy file to destination
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] storeProfilePhoto(MultipartFile photo, int userId) {
        if (photo == null || photo.isEmpty()) {
            throw new FileStorageException("Photo file is empty");
        }

        try {
            return photo.getBytes();
        } catch (IOException e) {
            throw new FileStorageException("Failed to read photo file: " + e.getMessage(), e);
        }
    }

    @Override
    public String storeResume(MultipartFile resume, int userId) {
        if (resume == null || resume.isEmpty()) {
            throw new FileStorageException("Resume file is empty");
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String fileName = "resume_" + userId + "_" + System.currentTimeMillis() + "_"
                    + resume.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Copy file to destination
            Files.copy(resume.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path for web access
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store resume: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Failed to delete file: " + filePath, e);
            return false;
        }
    }
}
