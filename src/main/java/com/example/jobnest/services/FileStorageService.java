package com.example.jobnest.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for handling file storage operations.
 * Encapsulates file upload/download logic.
 */
public interface FileStorageService {

    /**
     * Store a file and return its path.
     *
     * @param file      The file to store
     * @param directory Target directory
     * @param prefix    Filename prefix
     * @return Path to the stored file
     */
    String storeFile(MultipartFile file, String directory, String prefix);

    /**
     * Store profile photo for a user.
     *
     * @param photo  Profile photo file
     * @param userId User ID
     * @return Byte array of the photo
     */
    byte[] storeProfilePhoto(MultipartFile photo, int userId);

    /**
     * Store resume file for a user.
     *
     * @param resume Resume file
     * @param userId User ID
     * @return Path to the stored resume
     */
    String storeResume(MultipartFile resume, int userId);

    /**
     * Delete a file.
     *
     * @param filePath Path to the file to delete
     * @return true if deletion was successful
     */
    boolean deleteFile(String filePath);
}
