package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Application;
import com.example.demo.repository.ApplicationRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final FeedService feedService;

    public ApplicationService(ApplicationRepository applicationRepository, FeedService feedService) {
        this.applicationRepository = applicationRepository;
        this.feedService = feedService;
    }

    
    public void applyToFeed(String userId, String feedId, String part, String feedType) {
        // 신청 정보를 저장
        Application application = new Application();
        
        String userPk = "USER#" + userId;  // PK 및 UserID 통일
        application.setPk(userPk);  
        application.setSk("APPLICATION#" + feedId);  
        application.setEntityType("Application");  
        application.setPart(part); 
        application.setStatus("Pending");  // 기본 상태는 'Pending'
        application.setTimestamp(LocalDateTime.now());  
        application.setName("none");  // name 필드를 "none"으로 설정
       
        applicationRepository.save(application);
    
        // 신청 결과를 피드에 반영 (applyNum 증가)
        feedService.applyToFeed(feedId, part, feedType);  // feedType 추가. 안하고 해볼라 했는데 이상한 오류남..
    }
    
    
    
}

