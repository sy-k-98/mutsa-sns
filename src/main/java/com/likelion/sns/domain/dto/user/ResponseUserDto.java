package com.likelion.sns.domain.dto.user;

import com.likelion.sns.domain.entity.User;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class ResponseUserDto {
    private String username;
    private String password;
    private String profileImg;
    private String email;
    private String phone;

    public ResponseUserDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.profileImg = user.getProfileImg();
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }
}
