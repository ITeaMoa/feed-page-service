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

    // 대댓글 추가
    public void addReply(String feedId, String commentId, String userId, String content) {
        String replyId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        
        String pk = "FEED#" + feedId;
        String sk = "COMMENT#" + commentId + "#REPLY#" + replyId + "#" + now;

        // 유저 정보 조회
        UserProfile userProfile = userProfileRepository.findById("USER#" + userId, "PROFILE#");

        // ReplyEntity 생성
        ReplyEntity reply = new ReplyEntity();
        reply.setPk(pk);
        reply.setSk(sk);
        reply.setFeedId(feedId);
        reply.setCommentId(commentId);
        reply.setReplyId(replyId);
        reply.setUserId(userId);
        reply.setContent(content);
        reply.setTimestamp(now); 
        reply.setCreatorId("USER#" + userId); 

        if (userProfile != null) {
            reply.setNickname(userProfile.getNickname());
            reply.setUserStatus(userProfile.getUserStatus());
        } else {
            reply.setNickname("Unknown");
            reply.setUserStatus(true);
        }

        replyRepository.save(reply);
    }

    //특정 댓글의 대댓글 조회
    public List<ReplyEntity> getRepliesForComment(String feedId, String commentId) {
        return replyRepository.findByFeedIdAndCommentId(feedId, commentId);
    }

    //대댓글 삭제제
    public void deleteReply(String feedId, String commentId, String replyId, String userId) {
        String pk = "FEED#" + feedId;
    
        // 대댓글은 생성 시점의 timestamp가 sk에 포함되므로, 전체 리스트를 조회 후 비교
        List<ReplyEntity> replies = replyRepository.findByFeedIdAndCommentId(feedId, commentId);
        ReplyEntity target = replies.stream()
            .filter(r -> r.getReplyId().equals(replyId) && r.getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("삭제할 대댓글을 찾을 수 없거나 권한이 없습니다."));
    
        replyRepository.delete(target);
    }
}
