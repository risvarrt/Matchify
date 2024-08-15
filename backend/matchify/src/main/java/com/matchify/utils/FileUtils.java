package com.matchify.utils;


import com.matchify.exception.ImageTypeNotValidException;
import com.matchify.exception.ImageUploadException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    public static Path generateTempFilePath(MultipartFile file) {
        Path tempFilePath = null;
        try {
            tempFilePath = Files.createTempFile(file.getOriginalFilename(), "");
            file.transferTo(tempFilePath);
        } catch (IOException e) {
            throw new ImageUploadException("Error processing image file");
        }
        return tempFilePath;
    }

    // Validation for image, accepted format .png .jpg .jpeg
    public static boolean isImageFile(MultipartFile file) {
        if (file != null) {
            String fileName = file.getOriginalFilename();
            assert fileName != null;
            return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
        }
        throw new ImageTypeNotValidException("Images must be in the formats PNG, JPG, or JPEG.");
    }

    public static String getFileExtention(MultipartFile file) {
        try {
            String fileContent = file.getContentType();
            assert fileContent != null;
            String extension = fileContent.split("/")[1];
            return "." + extension;
        } catch (Exception exception) {
            throw new NullPointerException("File is empty");
        }
    }

    // Generating unique filename for the event, eg /{userid}/{userid}_{eventName}_{utc_timestamp}.png
    public static String generateUniqueFileNameForImage(
            String userId, String eventName, String fileExtension) {

      // Replace spaces in eventName with underscores
      String sanitizedEventName = eventName.replace(" ", "_");

      // Get the current UTC timestamp
      long currentTimeMillis = System.currentTimeMillis()/ 1000000 * 1000000;

      // Construct the unique filename
      return String.format("%s/%s_%s_%d%s",
              userId,
              userId,
              sanitizedEventName,
              currentTimeMillis,
              fileExtension
      );
    }

    public static String generateImageURL(String bucketName, String region, String fileName) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com" + "/" + fileName;
    }
}
