package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.ResponseDto;
import com.likelion.sns.domain.dto.TokenDto;
import com.likelion.sns.domain.dto.user.RequestUserLoginDto;
import com.likelion.sns.domain.dto.user.RequestUserRegisterDto;
import com.likelion.sns.jwt.CustomUserDetails;
import com.likelion.sns.jwt.JwtTokenUtils;
import com.likelion.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody RequestUserLoginDto dto) {
        UserDetails userDetails = manager.loadUserByUsername(dto.getUsername());
        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        TokenDto token = new TokenDto();
        token.setToken(jwtTokenUtils.generateToken(userDetails));
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RequestUserRegisterDto dto) {
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        manager.createUser(CustomUserDetails.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build());

        return ResponseEntity.ok(ResponseDto.getMessage("회원가입이 완료되었습니다."));
    }

    @PutMapping("/profile")
    ResponseEntity<ResponseDto> updateProfileImage(@RequestParam("image") MultipartFile profileImg, Authentication authentication) {
        String username = authentication.getName();
        userService.updateProfileImage(profileImg, username);
        return ResponseEntity.ok(ResponseDto.getMessage("프로필 이미지 등록이 완료되었습니다."));
    }
}
