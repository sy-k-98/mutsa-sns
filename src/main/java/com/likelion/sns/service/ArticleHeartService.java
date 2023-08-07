package com.likelion.sns.service;

import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.ArticleHeart;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.repository.ArticleHeartRepository;
import com.likelion.sns.repository.ArticleRepository;
import com.likelion.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleHeartService {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleHeartRepository articleHeartRepository;

    public String clickHeart(Long articleId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkUserNoMatch(article.getUser(), user.getUsername());

        if (!isAlreadyHeart(user, article)) {
            ArticleHeart articleHeart = new ArticleHeart(user, article);
            articleHeartRepository.save(articleHeart);
            return "좋아요를 눌렀습니다.";
        }
        else {
            articleHeartRepository.deleteByUserAndArticle(user, article);
            return "좋아요를 취소했습니다.";
        }
    }

    private void checkUserNoMatch(User user, String username) {
        if (user.getUsername().equals(username))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private Boolean isAlreadyHeart(User user, Article article) {
        return articleHeartRepository.existsByUserAndArticle(user, article);
    }
}
