package com.member_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    private final AmazonS3 amazonS3;

    // This grabs the bucket name from your application.properties
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${cloud.aws.region}")
    private String region;

    public FileStorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadProfilePicture(MultipartFile file, String userEmail) throws IOException {

        // 1. Generate a unique file name (e.g., profile-pictures/harsh@gym.com-12345.jpg)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFileName = "profile-pictures/" + userEmail + "-" + UUID.randomUUID() + extension;

        // 2. Tell S3 the size and type of the file
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // 3. Upload the actual file to your AWS S3 bucket
        amazonS3.putObject(new PutObjectRequest(bucketName, uniqueFileName, file.getInputStream(), metadata));

        // 4. Return the generated public Amazon link!
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + uniqueFileName;
    }
}