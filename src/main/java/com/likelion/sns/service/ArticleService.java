package com.likelion.sns.service;

import com.likelion.sns.domain.dto.article.RequestArticleDto;
import com.likelion.sns.domain.dto.article.ResponseArticleDto;
import com.likelion.sns.domain.dto.article.ResponseArticleListDto;
import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.ArticleImage;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.repository.ArticleImageRepository;
import com.likelion.sns.repository.ArticleRepository;
import com.likelion.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private static final String DEFAULT_IMAGE_URL = "src/main/resource/static/default_img.png";

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;
    private final ImageHandler imageHandler;

    public void createArticle(RequestArticleDto dto, List<MultipartFile> multipartFiles, String username) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Article article = new Article(user, dto.getTitle(), dto.getContent(), DEFAULT_IMAGE_URL);
        articleRepository.save(article);

        List<ArticleImage> articleImageList = imageHandler.parseFileInfo(article, multipartFiles);
        if (multipartFiles != null) {
            for (ArticleImage image : articleImageList) {
                article.addImage(articleImageRepository.save(image));
            }
            article.setThumbnail(articleImageList.get(0).getImageUrl());
        }
        articleRepository.save(article);
    }

    public void updateArticle(Long id, RequestArticleDto dto, List<MultipartFile> multipartFiles, String username) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Article article = articleRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkUserMatch(article.getUser(), user.getUsername());

        // 제목, 내용 변경
        article.update(dto.getTitle(), dto.getContent());

        List<ArticleImage> oldImageList = articleImageRepository.findAllByArticle(article);
        List<MultipartFile> newImageList = new ArrayList<>();

        // 기존에 등록한 이미지 없을 때
        if (CollectionUtils.isEmpty(oldImageList)) {
            // 새로 등록할 이미지가 있을 때 전부 등록
            if (!CollectionUtils.isEmpty(multipartFiles))
                newImageList.addAll(multipartFiles);
        }
        // 기존에 등록한 이미지가 있을 때
        else {
            // 새로 등록할 이미지가 없음 -> 삭제
            if (CollectionUtils.isEmpty(multipartFiles)) {
                for (ArticleImage image : oldImageList)
                    articleImageRepository.delete(image);
            }
            // 새로 등록할 이미지가 있을 때
            else {
                List<String> nameList = new ArrayList<>();
                // 기존 등록되어 있는 이미지 불러오기
                for (ArticleImage image : oldImageList) {
                    String name = image.getFilename();
                    // 새로 등록할 이미지 리스트에 이름이 없다면 삭제
                    if (!multipartFiles.contains(name))
                        articleImageRepository.delete(image);
                    // 아니면 등록
                    else
                        nameList.add(name);
                }

                for (MultipartFile file : multipartFiles) {
                    String name = file.getOriginalFilename();
                    if (!nameList.contains(name))
                        newImageList.add(file);
                }
            }
        }

        // 이미지 수정
        List<ArticleImage> articleImageList = imageHandler.parseFileInfo(article, newImageList);
        if (!articleImageList.isEmpty()) {
            for (ArticleImage image : articleImageList)
                article.addImage(articleImageRepository.save(image));
            article.setThumbnail(articleImageList.get(0).getImageUrl());
        } else {
            article.setThumbnail(DEFAULT_IMAGE_URL);
        }
        article.update(dto.getTitle(), dto.getContent());
        articleRepository.save(article);
    }

    public ResponseArticleDto readArticle(Long id, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Article article = articleRepository.findByIdAndDeleteAtIsNull(id);
        if (article == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        checkUserMatch(user, username);
        return ResponseArticleDto.fromEntity(article);
    }

    public List<ResponseArticleListDto> readArticleAll(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkUserMatch(user, username);
        List<Article> articles = articleRepository.findAllByUserAndDeleteAtIsNull(user);
        return articles.stream().map(ResponseArticleListDto::fromEntity).collect(Collectors.toList());
    }

    public void deleteArticle(Long id, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Article article = articleRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkUserMatch(article.getUser(), user.getUsername());
        article.setDeleteAt();
        articleRepository.save(article);
    }

    private void checkUserMatch(User user, String username) {
        if (!user.getUsername().equals(username))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
