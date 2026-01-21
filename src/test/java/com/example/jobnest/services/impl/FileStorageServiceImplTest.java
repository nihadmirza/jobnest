package com.example.jobnest.services.impl;

import com.example.jobnest.exception.FileStorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private final FileStorageServiceImpl fileStorageService = new FileStorageServiceImpl();

    @Test
    void storeFile_savesFileToProvidedDirectory() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes());

        String path = fileStorageService.storeFile(file, tempDir.toString(), "prefix");

        assertNotNull(path);
        assertTrue(path.contains(tempDir.toString()));
        assertTrue(Files.exists(Paths.get(path)));
    }

    @Test
    void storeFile_throwsForEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.txt", "text/plain", new byte[0]);

        assertThrows(FileStorageException.class,
                () -> fileStorageService.storeFile(emptyFile, tempDir.toString(), "prefix"));
    }

    @Test
    void storeProfilePhoto_returnsBytes() {
        MockMultipartFile photo = new MockMultipartFile(
                "photo", "photo.png", "image/png", new byte[]{1, 2, 3});

        byte[] result = fileStorageService.storeProfilePhoto(photo, 1);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void storeResume_writesFileAndReturnsRelativePath() throws Exception {
        MockMultipartFile resume = new MockMultipartFile(
                "resume", "cv.pdf", "application/pdf", "pdf".getBytes());

        String relativePath = fileStorageService.storeResume(resume, 99);

        assertTrue(relativePath.startsWith("/uploads/"));
        String fileName = relativePath.replace("/uploads/", "");
        Path stored = Paths.get("src/main/resources/static/uploads").resolve(fileName);
        assertTrue(Files.exists(stored));

        assertTrue(fileStorageService.deleteFile(stored.toString()));
    }

    @Test
    void deleteFile_returnsFalseWhenMissing() {
        assertFalse(fileStorageService.deleteFile("does-not-exist.txt"));
    }
}
