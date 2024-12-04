package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entity.UserProfile;
import com.example.demo.repository.UserProfileRepository;

@Service
public class UserService {
    private final UserProfileRepository userProfileRepository;

    public UserService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile getUserProfile(String pk, String sk) {
        return userProfileRepository.findById(pk, sk);
    }
}
