package com.likelion.sns.service;

import com.likelion.sns.domain.dto.article.ImageDto;
import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.ArticleImage;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImageHandler {
    public List<ArticleImage> parseFileInfo(Article article, List<MultipartFile> multipartFiles) throws Exception {
        List<ArticleImage> articleImageList = new ArrayList<>();
        if (multipartFiles != null) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String current_date = now.format(dateTimeFormatter);

            String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;
            String path = "images" + File.separator + current_date;

            File file = new File(path);
            if (!file.exists()) {
                boolean wasSuccessful = file.mkdirs();

                if (!wasSuccessful)
                    System.out.println("file: was not successful");
            }

            for (MultipartFile multipartFile : multipartFiles) {
                // 파일의 확장자 추출
                String originalFileExtension;
                String contentType = multipartFile.getContentType();

                // 확장자명이 존재하지 않을 경우 처리 x
                if (ObjectUtils.isEmpty(contentType)) {
                    break;
                } else {  // 확장자가 jpeg, png인 파일들만 받아서 처리
                    if (contentType.contains("image/jpeg"))
                        originalFileExtension = ".jpg";
                    else if (contentType.contains("image/png"))
                        originalFileExtension = ".png";
                    else  // 다른 확장자일 경우 처리 x
                        break;
                }

                // 파일명 중복 피하고자 나노초까지 얻어와 지정
                String new_file_name = System.nanoTime() + originalFileExtension;


                ImageDto imageDto = ImageDto.fromEntity(multipartFile, path + File.separator + new_file_name);
                ArticleImage articleImage = imageDto.toEntity();

                if (article.getId() != null)
                    articleImage.setArticle(article);

                articleImageList.add(articleImage);

                // 업로드 한 파일 데이터를 지정한 파일에 저장
                file = new File(absolutePath + path + File.separator + new_file_name);
                multipartFile.transferTo(file);
            }
        }
        return articleImageList;
    }
}
