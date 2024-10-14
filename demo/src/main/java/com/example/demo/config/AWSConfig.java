package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class AWSConfig {

    @Bean
    public DynamoDbClient dynamoDbClient() {
        //aws 자격증명 공급자 설정
        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

        //aws 지역 설정
        Region region = Region.of(System.getProperty("aws.region", "ap-northeast-2"));

        // DynamoDB 클라이언트를 빌더 패턴을 사용해 생성 및 반환
        // 자격증명 공급자와 리전을 지정하여 클라이언트를 구성
        return DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }
    
}
