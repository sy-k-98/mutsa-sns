package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.ResponseDto;
import com.likelion.sns.domain.dto.article.RequestArticleDto;
import com.likelion.sns.domain.dto.article.ResponseArticleDto;
import com.likelion.sns.domain.dto.article.ResponseArticleListDto;
import com.likelion.sns.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/users/{userId}/articles")
    ResponseEntity<ResponseDto> create(@PathVariable("userId") Long userId,
                                       @RequestPart RequestArticleDto dto,
                                       @RequestPart(required = false) List<MultipartFile> images,
                                       Authentication authentication) throws Exception {
        String username = authentication.getName();
        articleService.createArticle(dto, images, username);
        return ResponseEntity.ok(ResponseDto.getMessage("피드 등록이 완료되었습니다."));
    }

    // 목록 조회
    @GetMapping("/feed")
    public ResponseEntity<List<ResponseArticleListDto>> readAll(String username) {
        return ResponseEntity.ok(articleService.readArticleAll(username));
    }

    // 단일 조회
    @GetMapping("/users/{userId}/articles/{articleId}")
    public ResponseEntity<ResponseArticleDto> read(@PathVariable("userId") Long userId,
                                                   @PathVariable("articleId") Long articleId,
                                                   Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(articleService.readArticle(articleId, username));
    }

    @PutMapping("/users/{userId}/articles/{articleId}")
    public ResponseEntity<ResponseDto> update(@PathVariable("userId") Long userId,
                                              @PathVariable("articleId") Long articleId,
                                              @RequestPart RequestArticleDto dto,
                                              @RequestPart(required = false) List<MultipartFile> images,
                                              Authentication authentication) throws Exception {
        String username = authentication.getName();
        articleService.updateArticle(articleId, dto, images, username);
        return ResponseEntity.ok(ResponseDto.getMessage("피드 수정이 완료되었습니다."));
    }

    @DeleteMapping("/users/{userId}/articles/{articleId}")
    public ResponseEntity<ResponseDto> delete(@PathVariable("articleId") Long id, Authentication authentication) {
        String username = authentication.getName();
        articleService.deleteArticle(id, username);
        return ResponseEntity.ok(ResponseDto.getMessage("피드 삭제가 완료되었습니다."));

    }

}
