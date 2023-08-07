package com.likelion.sns.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ArticleImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Builder
    private ArticleImage(String filename, String imageUrl) {
        this.filename = filename;
        this.imageUrl = imageUrl;
    }

    public void setArticle(Article article) {
        this.article = article;
        if (!article.getArticleImages().contains(this)) {
            article.getArticleImages().add(this);
        }
    }
}
