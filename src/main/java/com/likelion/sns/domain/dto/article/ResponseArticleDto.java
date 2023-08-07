package com.likelion.sns.domain.dto.article;

import com.likelion.sns.domain.dto.comment.ResponseCommentDto;
import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.ArticleImage;
import com.likelion.sns.domain.entity.Comment;
import lombok.*;
import org.apache.catalina.valves.rewrite.RewriteCond;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<ResponseCommentDto> commentList;
    private Integer heartCounts;

    public static ResponseArticleDto fromEntity(Article article) {
        List<String> pathList = new ArrayList<>();
        for (ArticleImage img : article.getArticleImages()) {
            pathList.add(img.getImageUrl());
        }

        List<ResponseCommentDto> comments = new ArrayList<>();
        for (Comment comment : article.getComments()) {
            if (comment.getDeleteAt() == null)
                comments.add(ResponseCommentDto.fromEntity(comment));
        }

        return ResponseArticleDto.builder()
                .id(article.getId())
                .username(article.getUser().getUsername())
                .title(article.getTitle())
                .content(article.getContent())
                .articleImages(pathList)
                .commentList(comments)
                .heartCounts(article.getHearts().size())
                .build();
    }
}
