package com.likelion.sns.domain.dto.user;

import lombok.Data;

@Data
public class RequestUserRegisterDto {
    private String username;
    private String password;
    private String passwordCheck;
    private String email;
    private String phone;
}
