package com.likelion.sns.repository;

import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.ArticleHeart;
import com.likelion.sns.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ArticleHeartRepository extends JpaRepository<ArticleHeart, Long> {
    Boolean existsByUserAndArticle(User user, Article article);
    @Transactional
    void deleteByUserAndArticle(User user, Article article);
}
