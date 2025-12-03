package ru.bmstu.iu6.culinarycompanion.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUploadUtil {
    
    private static final String UPLOAD_DIR = "uploads/images/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    public static String saveFile(InputStream inputStream, String originalFilename) throws IOException {
        validateFile(originalFilename);
        
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "/uploads/images/" + filename;
    }
    
    public static void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + fileUrl);
        }
    }
    
    private static void validateFile(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IOException("Filename is required");
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        boolean isAllowed = false;
        
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExt)) {
                isAllowed = true;
                break;
            }
        }
        
        if (!isAllowed) {
            throw new IOException("File type not allowed. Allowed types: jpg, jpeg, png, gif, webp");
        }
    }
    
    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}
