package com.likelion.sns.domain.dto.article;

import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.ArticleImage;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseArticleDto {
    private Long id;
    private String username;
    private String title;
    private String content;
    private List<String> articleImages;

    public static ResponseArticleDto fromEntity(Article article) {
        List<String> pathList = new ArrayList<>();
        for (ArticleImage img : article.getArticleImages()) {
            pathList.add(img.getImageUrl());
        }

        return ResponseArticleDto.builder()
                .id(article.getId())
                .username(article.getUser().getUsername())
                .title(article.getTitle())
                .content(article.getContent())
                .articleImages(pathList)
                .build();
    }
}
