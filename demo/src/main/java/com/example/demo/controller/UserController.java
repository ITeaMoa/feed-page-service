package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.UserProfile;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable("userId") String userId) {
        String pk = "USER#" + userId; // Partition Key
        String sk = "INFO#"; // Sort Key
        UserProfile userProfile = userService.getUserProfile(pk, sk);

        if (userProfile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userProfile);
    }
    
}
