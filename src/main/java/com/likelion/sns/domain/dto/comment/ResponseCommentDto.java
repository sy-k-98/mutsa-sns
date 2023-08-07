package com.likelion.sns.domain.dto.comment;

import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCommentDto {
    private String username;
    private String content;

    public static ResponseCommentDto fromEntity(Comment comment) {
        return ResponseCommentDto.builder()
                .username(comment.getUser().getUsername())
                .content(comment.getContent())
                .build();
    }
}
