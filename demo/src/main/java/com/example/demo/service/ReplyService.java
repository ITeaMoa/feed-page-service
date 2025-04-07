package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.entity.ReplyEntity;
import com.example.demo.entity.UserProfile;
import com.example.demo.repository.ReplyRepository;
import com.example.demo.repository.UserProfileRepository;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserProfileRepository userProfileRepository;

    public ReplyService(ReplyRepository replyRepository, UserProfileRepository userProfileRepository) {
        this.replyRepository = replyRepository;
        this.userProfileRepository = userProfileRepository;
        
    }
     public void addReply(String feedId, String commentId, String userId, String content) {
        String replyId = UUID.randomUUID().toString();
        String timestamp = LocalDateTime.now().toString();

        String pk = "FEED#" + feedId;
        String sk = "COMMENT#" + commentId + "#REPLY#" + replyId + "#" + timestamp;

        UserProfile userProfile = userProfileRepository.findById("USER#" + userId, "PROFILE#");

        ReplyEntity reply = new ReplyEntity();
        reply.setPk(pk);
        reply.setSk(sk);
        reply.setFeedId(feedId);
        reply.setCommentId(commentId);
        reply.setReplyId(replyId);
        reply.setUserId(userId);
        reply.setContent(content);
        reply.setTimestamp(LocalDateTime.parse(timestamp));
        if (userProfile != null) {
            reply.setNickname(userProfile.getNickname());         
            reply.setUserStatus(userProfile.getUserStatus());     
        } else {
            reply.setNickname("Unknown");                         
            reply.setUserStatus(true);                            
        }
        

        replyRepository.save(reply);
    }

    public List<ReplyEntity> getRepliesForComment(String feedId, String commentId) {
        return replyRepository.findByFeedIdAndCommentId(feedId, commentId);
    }
    
}
