package com.likelion.sns.service;

import com.likelion.sns.domain.dto.comment.RequestCommentDto;
import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.Comment;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.repository.ArticleRepository;
import com.likelion.sns.repository.CommentRepository;
import com.likelion.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public void addComment(Long articleId, RequestCommentDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Comment comment = new Comment(user, article, dto.getContent());
        commentRepository.save(comment);
    }

    public void updateComment(Long articleId, Long commentId, RequestCommentDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkSameArticle(article, comment);
        checkUserMatch(user, comment.getUser().getUsername());
        comment.update(dto.getContent());
        commentRepository.save(comment);
    }

    public void deleteComment(Long articleId, Long commentId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkSameArticle(article, comment);
        checkUserMatch(user, comment.getUser().getUsername());
        comment.setDeleteAt();
        commentRepository.save(comment);
    }

    private void checkSameArticle(Article article, Comment comment) {
        if (!article.getId().equals(comment.getArticle().getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private void checkUserMatch(User user, String username) {
        if (!user.getUsername().equals(username))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
