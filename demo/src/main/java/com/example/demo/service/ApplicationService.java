package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ApplicationDto;
import com.example.demo.entity.Application;
import com.example.demo.entity.FeedEntity;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.FeedRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final FeedService feedService;
    private final FeedRepository feedRepository;

    public ApplicationService(ApplicationRepository applicationRepository, FeedService feedService, FeedRepository feedRepository) {
        this.applicationRepository = applicationRepository;
        this.feedService = feedService;
        this.feedRepository = feedRepository;
    }

    
    public void applyToFeed(String userId, String feedId, String part, String feedType) {

        String userPk = "USER#" + userId;
    String applicationSk = "APPLICATION#" + feedId;

    //이미 신청했는지 확인 코드 추가
    List<Application> existingApplications = applicationRepository.findByUserPk(userPk);
    boolean isAlreadyApplied = existingApplications.stream()
            .anyMatch(app -> app.getSk().equals(applicationSk));

    if (isAlreadyApplied) {
        throw new RuntimeException("이미 해당 피드에 신청하셨습니다.");
    }
        // 신청 정보를 저장
        Application application = new Application();
        
        // String userPk = "USER#" + userId;  // PK 및 UserID 통일
        application.setPk(userPk);  
        application.setSk("APPLICATION#" + feedId);  
        application.setEntityType("APPLICATION");  
        application.setPart(part); 
        application.setStatus("PENDING");  //기본상태태
        application.setTimestamp(LocalDateTime.now());  

       
        applicationRepository.save(application);
    
        // 신청 결과를 피드에 반영 (applyNum 증가)
        feedService.applyToFeed(feedId, part, feedType);  // feedType 추가. 안하고 해볼라 했는데 이상한 오류남..
    }

   
    public List<ApplicationDto> getApplicationsWithFeedInfoByUserId(String userId) {
        String userPk = "USER#" + userId;
        List<Application> applications = applicationRepository.findByUserPk(userPk);

        return applications.stream()
                .map(application -> {
                    String feedId = application.getSk().replace("APPLICATION#", "");
                    String feedPk = "FEED#" + feedId;

                    // FeedService를 사용하여 피드 조회
                    FeedEntity feedEntity = feedService.findFeedByPk(feedPk);

                    if (feedEntity == null) {
                        return createEmptyApplicationDto(application, feedId);
                    }

                    return mapToApplicationDto(application, feedEntity);
                })
                .collect(Collectors.toList());
    }

    private ApplicationDto createEmptyApplicationDto(Application application, String feedId) {
        ApplicationDto dto = new ApplicationDto();
        dto.setUserId(application.getPk().replace("USER#", ""));
        dto.setFeedId(feedId);
        dto.setPart(application.getPart());
        dto.setStatus(application.getStatus());
        

        // 피드 관련 정보는 빈 값으로 설정
        dto.setCreatorId(null);
        dto.setTitle(null);
        dto.setContent(null);
        dto.setTags(null);
        dto.setRecruitmentNum(null);
        dto.setDeadline(null);
        dto.setPeriod(null);
        dto.setLikesCount(null);
        dto.setRecruitmentRoles(null);
        dto.setNickname(null);
        return dto;
    }

    private ApplicationDto mapToApplicationDto(Application application, FeedEntity feedEntity) {
        ApplicationDto dto = new ApplicationDto();
        dto.setUserId(application.getPk().replace("USER#", ""));
        dto.setFeedId(application.getSk().replace("APPLICATION#", ""));
        dto.setPart(application.getPart());
        dto.setStatus(application.getStatus());

        // FeedEntity 정보 추가
        dto.setCreatorId(feedEntity.getCreatorId());
        dto.setTitle(feedEntity.getTitle());
        dto.setContent(feedEntity.getContent());
        dto.setTags(feedEntity.getTags());
        dto.setRecruitmentNum(feedEntity.getRecruitmentNum());
        dto.setDeadline(feedEntity.getDeadline());
        dto.setPeriod(feedEntity.getPeriod());
        dto.setLikesCount(feedEntity.getLikesCount());
        dto.setRecruitmentRoles(feedEntity.getRecruitmentRoles());

        //  닉네임 추가 (null 체크)
        if (feedEntity.getNickname() != null && !feedEntity.getNickname().equals("Unknown")) {
            dto.setNickname(feedEntity.getNickname());
        } else {
            dto.setNickname("Unknown");
        }

        //  신청 시간 추가
        dto.setApplicationTimestamp(application.getTimestamp());
        return dto;
    }

    public void cancelApplication(String userId, String feedId) {
        String userPk = "USER#" + userId;
        String applicationSk = "APPLICATION#" + feedId;
    
        //신청정보 조회회
        List<Application> applications = applicationRepository.findByUserPk(userPk);
        Application applicationToCancel = applications.stream()
                .filter(app -> app.getSk().equals(applicationSk))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("신청 내역이 없습니다."));
    
        // 신청 상태가 PENDING 상태일 때만 취소 가능
        if (!"PENDING".equals(applicationToCancel.getStatus())) {
            throw new RuntimeException("이미 처리된 신청은 취소할 수 없습니다.");
        }
    
      
        applicationRepository.delete(applicationToCancel);
    
        //피드의 신청자 수 감소 
        feedService.cancelApplicationInFeed(feedId, applicationToCancel.getPart());
    }
    

}

