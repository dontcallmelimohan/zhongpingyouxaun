package com.zpyx.zhongpingyouxuan.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtil {
    
    
    public static final String PRODUCTS_IMG_DIR = "src/main/resources/static/productsImg/";
    
    
    public static final String BANNER_IMG_DIR = "src/main/resources/static/bannerimg/";
    
    
    public static String saveFile(MultipartFile file, String uploadDir) throws IOException {
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        
        return fileName;
    }
    
    
    public static void deleteFile(String fileName, String uploadDir) throws IOException {
        if (fileName != null && !fileName.trim().isEmpty()) {
            Path filePath = Paths.get(uploadDir, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        }
    }
}