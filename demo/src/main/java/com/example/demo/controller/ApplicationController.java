package com.example.demo.controller;



import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApplicationDto;
import com.example.demo.entity.Application;
import com.example.demo.service.ApplicationService;

@RestController
@RequestMapping("/feed")
public class ApplicationController {
    private final ApplicationService applicationService;
    

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply")
    public ResponseEntity<String> applyToFeed(
    @RequestParam("feedType") String feedType, // feedType을 명시적으로
    @RequestBody ApplicationDto applicationDto) {

   //dto에서 가져온값
    applicationService.applyToFeed(applicationDto.getUserId(), applicationDto.getFeedId(), applicationDto.getPart(), feedType);

    return ResponseEntity.ok("신청이 완료됐습니다.");
}

    // 특정 유저의 신청 내역 조회 엔드포인트
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getApplicationsByUserId(@RequestParam("userId") String userId) {
     List<Application> applications = applicationService.getApplicationsByUserId(userId);
     return ResponseEntity.ok(applications);
 }

}
