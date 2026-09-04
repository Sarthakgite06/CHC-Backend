package com.onkar.chc.servicetest;

import com.onkar.chc.service.FileStorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.file.Paths;

public class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @BeforeEach
    public void setUp() {
        fileStorageService = new FileStorageService();
    }

    @Test
    public void testStoreFileSuccess() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-report.pdf",
                "application/pdf",
                "Dummy content for test".getBytes()
        );

        String storedFileName = fileStorageService.storeFile(file);
        Assertions.assertNotNull(storedFileName);
        Assertions.assertTrue(storedFileName.endsWith(".pdf"));

        // Clean up test file if created
        File savedFile = Paths.get("uploads", storedFileName).toFile();
        if (savedFile.exists()) {
            savedFile.delete();
        }
    }

    @Test
    public void testStoreAndLoadFile() {
        byte[] content = "Hello world health record".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                content
        );

        String storedFileName = fileStorageService.storeFile(file);
        Resource resource = fileStorageService.loadFileAsResource(storedFileName);

        Assertions.assertNotNull(resource);
        Assertions.assertTrue(resource.exists());

        // Clean up
        File savedFile = Paths.get("uploads", storedFileName).toFile();
        if (savedFile.exists()) {
            savedFile.delete();
        }
    }

    @Test
    public void testLoadNonExistentFileThrowsException() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            fileStorageService.loadFileAsResource("non-existent-file-12345.pdf");
        });
    }

    @Test
    public void testStoreFileWithInvalidExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "noextension",
                "text/plain",
                "content".getBytes()
        );

        String storedFileName = fileStorageService.storeFile(file);
        Assertions.assertNotNull(storedFileName);

        File savedFile = Paths.get("uploads", storedFileName).toFile();
        if (savedFile.exists()) {
            savedFile.delete();
        }
    }

    @Test
    public void testStoreDangerousExtensionThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "malicious.exe",
                "application/octet-stream",
                "dangerous executable code".getBytes()
        );

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(file);
        });
        Assertions.assertTrue(ex.getMessage().contains("Dangerous file type not allowed"));
    }
}
