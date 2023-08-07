package com.likelion.sns.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String thumbnail;

    private LocalDateTime deleteAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "article",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true)
    private List<ArticleImage> articleImages = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<ArticleHeart> hearts = new ArrayList<>();

    @Builder
    public Article(User user, String title, String content, String thumbnail) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
    }

    public void setThumbnail(String imgPath) {
        this.thumbnail = imgPath;
    }

    public void setDeleteAt() {
        this.deleteAt = LocalDateTime.now();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addImage(ArticleImage articleImage) {
        this.articleImages.add(articleImage);
        if (articleImage.getArticle() != this) {
            articleImage.setArticle(this);
        }
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        if (comment.getArticle() != this) {
            comment.setArticle(this);
        }
    }
}
