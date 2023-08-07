package com.likelion.sns.domain.dto.article;

import com.likelion.sns.domain.entity.Article;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestArticleDto {
    private String title;
    private String content;
}
