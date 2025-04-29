package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.CommentAddDTO;
import com.dw.artgallery.model.User;
import com.dw.artgallery.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    CommentService commentService;


    // Comment 추가
    @PostMapping("/add")
    public ResponseEntity<CommentAddDTO> addComment(@RequestBody CommentAddDTO dto,
                                                    @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(commentService.addComment(dto, user), HttpStatus.CREATED);
    }

    // Comment commentId 로 수정
    @PutMapping("/update/{commentId}")
    public ResponseEntity<CommentAddDTO> updateComment(@PathVariable Long commentId,
                                                       @RequestBody CommentAddDTO dto,
                                                       @AuthenticationPrincipal User user) {
        CommentAddDTO updated = commentService.updateComment(commentId, dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);

    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId,
                                                @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    @PostMapping("/deleted/{commentId}")
    public ResponseEntity<String> deletedComment(@PathVariable Long commentId,
                                                 @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(commentService.deletedComment(commentId), HttpStatus.OK);
    }


}
