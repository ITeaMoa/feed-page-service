package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.constant.StatusType;
import com.example.demo.dto.ApplicationDto;
import com.example.demo.entity.Application;
import com.example.demo.entity.FeedEntity;
import com.example.demo.repository.ApplicationRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final FeedService feedService;

    public ApplicationService(ApplicationRepository applicationRepository, FeedService feedService) {
        this.applicationRepository = applicationRepository;
        this.feedService = feedService;
    }

    // 신청
    public void applyToFeed(String userId, String feedId, String part, String feedType) {
        String userPk = "USER#" + userId;
        String applicationSk = "APPLICATION#" + feedId;

        List<Application> existingApplications = applicationRepository.findByUserPk(userPk);
        boolean isAlreadyApplied = existingApplications.stream()
                .anyMatch(app -> app.getSk().equals(applicationSk));

        if (isAlreadyApplied) {
            throw new RuntimeException("이미 해당 피드에 신청하셨습니다.");
        }

        Application application = new Application();
        application.setPk(userPk);
        application.setSk(applicationSk);
        application.setPart(part);
        application.setStatus(StatusType.PENDING); // enum 사용
        application.setTimestamp(LocalDateTime.now());
        application.setCreatorId("USER#" + userId);
        application.setUserStatus(true);

        applicationRepository.save(application);

        feedService.applyToFeed(feedId, part, feedType);
    }

    // 신청 목록 조회
    public List<ApplicationDto> getApplicationsWithFeedInfoByUserId(String userId) {
        String userPk = "USER#" + userId;
        List<Application> applications = applicationRepository.findByUserPk(userPk);

        return applications.stream()
                .map(application -> {
                    String feedId = application.getSk().replace("APPLICATION#", "");
                    String feedPk = "FEED#" + feedId;

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
        dto.setStatus(application.getStatus()); // enum 그대로 전달
        dto.setApplicationTimestamp(application.getTimestamp());

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
        dto.setApplicationTimestamp(application.getTimestamp());

        dto.setCreatorId(feedEntity.getCreatorId());
        dto.setTitle(feedEntity.getTitle());
        dto.setContent(feedEntity.getContent());
        dto.setTags(feedEntity.getTags());
        dto.setRecruitmentNum(feedEntity.getRecruitmentNum());
        dto.setDeadline(feedEntity.getDeadline());
        dto.setPeriod(feedEntity.getPeriod());
        dto.setLikesCount(feedEntity.getLikesCount());
        dto.setRecruitmentRoles(feedEntity.getRecruitmentRoles());
        dto.setNickname(feedEntity.getNickname() != null ? feedEntity.getNickname() : "Unknown");

        return dto;
    }

    // 신청 취소
    public void cancelApplication(String userId, String feedId) {
        String userPk = "USER#" + userId;
        String applicationSk = "APPLICATION#" + feedId;

        List<Application> applications = applicationRepository.findByUserPk(userPk);
        Application applicationToCancel = applications.stream()
                .filter(app -> app.getSk().equals(applicationSk))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("신청 내역이 없습니다."));

        if (applicationToCancel.getStatus() != StatusType.PENDING) {
            throw new RuntimeException("이미 처리된 신청은 취소할 수 없습니다.");
        }

        applicationRepository.delete(applicationToCancel);
        feedService.cancelApplicationInFeed(feedId, applicationToCancel.getPart());
    }

    // 수락된 신청 목록
    public List<ApplicationDto> getAcceptedApplications(String userId) {
        String userPk = "USER#" + userId;
        List<Application> applications = applicationRepository.findByUserPk(userPk);

        return applications.stream()
                .filter(app -> app.getStatus() == StatusType.ACCEPTED)
                .map(application -> {
                    String feedId = application.getSk().replace("APPLICATION#", "");
                    String feedPk = "FEED#" + feedId;
                    FeedEntity feedEntity = feedService.findFeedByPk(feedPk);
                    if (feedEntity == null) {
                        return createEmptyApplicationDto(application, feedId);
                    }
                    return mapToApplicationDto(application, feedEntity);
                })
                .collect(Collectors.toList());
    }

    // 거절된 신청 목록
    public List<ApplicationDto> getRejectedApplications(String userId) {
        String userPk = "USER#" + userId;
        List<Application> applications = applicationRepository.findByUserPk(userPk);

        return applications.stream()
                .filter(app -> app.getStatus() == StatusType.REJECTED)
                .map(application -> {
                    String feedId = application.getSk().replace("APPLICATION#", "");
                    String feedPk = "FEED#" + feedId;
                    FeedEntity feedEntity = feedService.findFeedByPk(feedPk);
                    if (feedEntity == null) {
                        return createEmptyApplicationDto(application, feedId);
                    }
                    return mapToApplicationDto(application, feedEntity);
                })
                .collect(Collectors.toList());
    }

    // 탈퇴한 유저의 신청 정리
    public void cleanUpApplicationsByDeletedUser(String userId) {
        String userPk = "USER#" + userId;
        List<Application> applications = applicationRepository.findByUserPk(userPk);

        for (Application application : applications) {
            if (Boolean.FALSE.equals(application.getUserStatus())) {
                String feedId = application.getSk().replace("APPLICATION#", "");

                if (application.getStatus() == StatusType.ACCEPTED) {
                    feedService.cancelApplicationInFeed(feedId, application.getPart());
                }

                applicationRepository.delete(application);
            }
        }
    }
}
