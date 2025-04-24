//package com.dw.artgallery.model;
//
//
//import com.amazonaws.services.s3.model.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class S3Uploader {
//
//    private final S3Client s3Client;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    public String upload(MultipartFile file, String dirName) throws IOException {
//        String filename = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        s3Client.putObject(
//                PutObjectRequest.builder()
//                        .bucket(bucket)
//                        .key(filename)
//                        .acl("public-read")
//                        .contentType(file.getContentType())
//                        .build(),
//                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
//        );
//
//        return "https://" + bucket + ".s3.amazonaws.com/" + filename;
//    }
//}