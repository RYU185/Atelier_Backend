//package com.dw.artgallery.controller;
//
//import com.dw.artgallery.model.S3Uploader;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//public class UploadController {
//
//    private final S3Uploader s3Uploader;
//
//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
//        try {
//            String url = s3Uploader.upload(file, "community");
//            return ResponseEntity.ok(Map.of("url", url));
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().body(Map.of("error", "Upload failed"));
//        }
//    }
//}