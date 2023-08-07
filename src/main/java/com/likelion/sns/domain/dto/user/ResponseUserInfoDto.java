package com.likelion.sns.domain.dto.user;

import com.likelion.sns.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserInfoDto {
    private String username;
    private String profileImg;

    @Builder
    public static ResponseUserInfoDto fromEntity(User user) {
        return  ResponseUserInfoDto.builder()
                .username(user.getUsername())
                .profileImg(user.getProfileImg())
                .build();
    }
}
