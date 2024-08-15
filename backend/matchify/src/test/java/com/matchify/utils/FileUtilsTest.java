package com.matchify.utils;

import com.matchify.exception.ImageTypeNotValidException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileUtilsTest {

    @Mock
    MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        // Initialize the mock object before each test method
        multipartFile = mock(MultipartFile.class);
    }

    @Test
    void testGenerateTempFilePath() {
        try {
            when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
            Path tempFilePath = FileUtils.generateTempFilePath(multipartFile);
            assertNotNull(tempFilePath);
            assertTrue(Files.exists(tempFilePath));
            Files.deleteIfExists(tempFilePath);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void testIsImageFile() {
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        assertTrue(FileUtils.isImageFile(multipartFile));
    }


    @Test
    void testGetFileExtention() {
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        assertEquals(".jpeg", FileUtils.getFileExtention(multipartFile));
    }

    @Test
    void testGenerateUniqueFileNameForImage() {
        String userId = "user1";
        String eventName = "Test Event";
        String fileExtension = ".jpg";
        String expectedFileNameRegex = "user1/user1_Test_Event_\\d{13}\\.jpg";

        String uniqueFileName = FileUtils.generateUniqueFileNameForImage(userId, eventName, fileExtension);

        assertNotNull(uniqueFileName);
        assertTrue(uniqueFileName.matches(expectedFileNameRegex));
    }

    @Test
    void testGenerateImageURL() {
        String bucketName = "example-bucket";
        String region = "us-east-1";
        String fileName = "user1/user1_Test_Event_1234567890123.jpg";

        String expectedURL = "https://example-bucket.s3.us-east-1.amazonaws.com/user1/user1_Test_Event_1234567890123.jpg";
        String generatedURL = FileUtils.generateImageURL(bucketName, region, fileName);

        assertEquals(expectedURL, generatedURL);
    }

}
