package com.dw.artgallery.service;

import com.dw.artgallery.DTO.CommentAddDTO;
import com.dw.artgallery.exception.ResourceNotFoundException;
import com.dw.artgallery.model.Comment;
import com.dw.artgallery.model.Community;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.CommentRepository;
import com.dw.artgallery.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommunityRepository communityRepository;

    public CommentAddDTO addComment(CommentAddDTO dto, User user) {
        Community community = communityRepository.findById(dto.getCommunityId())
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티 글이 존재하지 않습니다."));

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setUser(user);
        comment.setCommunity(community);
        comment.setCreationDate(LocalDateTime.now());
        Comment saved = commentRepository.save(comment);
        CommentAddDTO responseDTO = new CommentAddDTO();
        responseDTO.setCommentId(saved.getId());
        responseDTO.setText(saved.getText());
        responseDTO.setUserNickname(user.getNickName());
        responseDTO.setCreationDate(saved.getCreationDate());
        responseDTO.setCommunityId(dto.getCommunityId());

        return responseDTO;
    }

    public CommentAddDTO updateComment(Long commentId, CommentAddDTO dto, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("본인의 댓글만 수정할 수 있습니다.");
        }
        Community community = communityRepository.findById(dto.getCommunityId())
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티 글이 존재하지 않습니다."));
        comment.setText(dto.getText());
        comment.setCommunity(community);
        comment.setCreationDate(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);

        CommentAddDTO responseDTO = new CommentAddDTO();
        responseDTO.setCommentId(updated.getId());
        responseDTO.setCommunityId(updated.getCommunity().getId());
        responseDTO.setText(updated.getText());
        responseDTO.setUserNickname(user.getNickName());
        responseDTO.setCreationDate(updated.getCreationDate());

        return responseDTO;
    }


    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));


        if (!comment.getUser().getUserId().equals(user.getUserId()) && !user.isAdmin()) {
            throw new SecurityException("본인 또는 관리자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }


    public String deletedComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));


        if (!comment.getUser().getUserId().equals(user.getUserId()) && !user.isAdmin()) {
            throw new SecurityException("본인 또는 관리자만 삭제할 수 있습니다.");
        }

        comment.setIsDeleted(true);
        commentRepository.save(comment);
        return "댓글이 논리적으로 삭제되었습니다.";
    }
}