package com.likelion.sns.domain.dto.comment;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCommentDto {
    private String content;
}
