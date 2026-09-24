package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.config.CloudinaryProperties;
import com.aditya.siteexpensemanager.service.FileStorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;
    public FileStorageServiceImpl(CloudinaryProperties cloudinaryProperties) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudinaryProperties.getCloudName(),
                "api_key", cloudinaryProperties.getApiKey(),
                "api_secret", cloudinaryProperties.getApiSecret(),
                "secure", true,
                "timeout", 15000,
                "connectTimeout", 10000
        ));
    }

    @Override
    @Retryable(
            retryFor = IOException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        boolean isImage = contentType != null && contentType.startsWith("image/");
        boolean isPdf = contentType != null && contentType.equals("application/pdf");

        if (!isImage && !isPdf) {
            throw new IllegalArgumentException("Only image or PDF files are allowed");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File must be under 10MB");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "site-expense-manager/bills",
                            "resource_type", "auto"
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }
}