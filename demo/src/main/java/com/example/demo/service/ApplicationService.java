package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

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

       
        applicationRepository.save(application);
    
        // 신청 결과를 피드에 반영 (applyNum 증가)
        feedService.applyToFeed(feedId, part, feedType);  // feedType 추가. 안하고 해볼라 했는데 이상한 오류남..
    }

    // 특정 유저의 신청 내역 조회 일단 로그인 안해서 이렇게
    public List<Application> getApplicationsByUserId(String userId) {
        String userPk = "USER#" + userId;
        return applicationRepository.findByUserPk(userPk);
    }
    
    
    
}

