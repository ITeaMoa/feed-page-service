package com.example.demo.service;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) {
        try {
            // 고유한 파일 키 생성
            String key = "feeds/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 업로드 요청 생성
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            InputStream inputStream = file.getInputStream();
            PutObjectResponse response = s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, file.getSize())
            );

            // 업로드된 파일 URL 생성
            String uploadedUrl = s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketName).key(key))
                    .toExternalForm();

            //로그 출력해보겠다다
            System.out.println("✅ S3 업로드 완료");
            System.out.println("➡ 파일 Key: " + key);
            System.out.println("➡ 업로드 응답: " + response);
            System.out.println("➡ 업로드된 URL: " + uploadedUrl);

            return uploadedUrl;

        } catch (S3Exception s3e) {
            System.out.println("❌ S3 오류: " + s3e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 업로드 실패: " + s3e.getMessage(), s3e);
        } catch (Exception e) {
            System.out.println("❌ 예외 발생: " + e.getMessage());
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }
}
