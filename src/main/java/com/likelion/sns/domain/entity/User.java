package com.likelion.sns.domain.entity;

import com.likelion.sns.jwt.CustomUserDetails;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String profileImg;

    private String email;

    private String phone;

    @OneToMany(mappedBy = "user")
    private List<Article> articles;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void updateInfo(CustomUserDetails userDetails) {
        this.email = userDetails.getEmail();
        this.phone = userDetails.getPhone();
    }
}
