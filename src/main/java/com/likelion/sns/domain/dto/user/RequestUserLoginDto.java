package com.likelion.sns.domain.dto.user;

import lombok.Data;

@Data
public class RequestUserLoginDto {
    private String username;
    private String password;
}
