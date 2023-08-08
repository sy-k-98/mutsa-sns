package com.likelion.sns.domain.dto.article;

import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.ArticleImage;
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
public class ResponseArticleListDto {
    private Long id;
    private String username;
    private String title;
    private String content;
    private String thumbnail;

    public static ResponseArticleListDto fromEntity(Article article) {
        return ResponseArticleListDto.builder()
                .id(article.getId())
                .username(article.getUser().getUsername())
                .title(article.getTitle())
                .content(article.getContent())
                .thumbnail(article.getThumbnail())
                .build();
    }
}
