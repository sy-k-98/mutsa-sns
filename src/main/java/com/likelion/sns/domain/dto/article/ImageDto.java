package com.likelion.sns.domain.dto.article;

import com.likelion.sns.domain.entity.Article;
import com.likelion.sns.domain.entity.ArticleImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private String filename;
    private String imageUrl;

    public static ImageDto fromEntity(MultipartFile multipartFile, String path) {
        return ImageDto.builder()
                .filename(multipartFile.getOriginalFilename())
                .imageUrl(path)
                .build();
    }

    public ArticleImage toEntity() {
        return ArticleImage.builder()
                .filename(filename)
                .imageUrl(imageUrl)
                .build();
    }
}
