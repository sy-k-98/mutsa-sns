package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.ResponseDto;
import com.likelion.sns.domain.dto.comment.RequestCommentDto;
import com.likelion.sns.domain.entity.Comment;
import com.likelion.sns.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("feeds/{articleId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseDto> create(@PathVariable("articleId") Long articleId,
                                              @RequestBody RequestCommentDto dto,
                                              Authentication authentication) {
        String username = authentication.getName();
        commentService.addComment(articleId, dto, username);
        return ResponseEntity.ok(ResponseDto.getMessage("댓글이 등록되었습니다."));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseDto> update(@PathVariable("articleId") Long articleId,
                                              @PathVariable("commentId") Long commentId,
                                              @RequestBody RequestCommentDto dto,
                                              Authentication authentication) {
        String username = authentication.getName();
        commentService.updateComment(articleId, commentId, dto, username);
        return ResponseEntity.ok(ResponseDto.getMessage("댓글이 수정되었습니다."));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto> delete(@PathVariable("articleId") Long articleId,
                                              @PathVariable("commentId") Long commentId,
                                              Authentication authentication) {
        String username = authentication.getName();
        commentService.deleteComment(articleId, commentId, username);
        return ResponseEntity.ok(ResponseDto.getMessage("댓글을 삭제했습니다."));
    }
}
