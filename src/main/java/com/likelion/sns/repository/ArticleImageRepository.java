package com.likelion.sns.repository;

import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {
    List<ArticleImage> findAllByArticle(Article article);
}
