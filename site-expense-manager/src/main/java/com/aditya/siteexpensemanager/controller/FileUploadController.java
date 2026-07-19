package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "Upload a bill/invoice image or PDF, returns its public URL")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {
        String url = fileStorageService.uploadFile(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}