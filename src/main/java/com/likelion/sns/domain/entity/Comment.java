package com.likelion.sns.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    private String content;

    private LocalDateTime deleteAt;

    @Builder
    public Comment(User user, Article article, String content) {
        this.user = user;
        this.article = article;
        this.content = content;
    }
    public void setDeleteAt() {
        this.deleteAt = LocalDateTime.now();
    }

    public void update(String content) {
        this.content = content;
    }

    public void setArticle(Article article) {
        this.article = article;
        if (!article.getComments().contains(this)) {
            article.getComments().add(this);
        }
    }
}
