package com.likelion.sns.repository;

import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByUserAndDeleteAtIsNull(User user);

    Article findByIdAndDeleteAtIsNull(Long id);

}
