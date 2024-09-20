package com.project.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PictureUploadService {

    @Value("${upload.path}")
    private String uploadPath;

    public String uploadProfilePicture(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String fileName = LocalDate.now() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadPath + fileName);
        Files.write(path, bytes);
        return fileName;
    }
    
    public byte[] getProfilePicture(String fileName) throws IOException {
        Path path = Paths.get(uploadPath + fileName);
        return Files.readAllBytes(path);
    }
}
