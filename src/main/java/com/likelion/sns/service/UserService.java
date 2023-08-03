package com.likelion.sns.service;

import com.likelion.sns.domain.dto.user.RequestUserLoginDto;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void updateProfileImage(Long id, RequestUserLoginDto dto, MultipartFile profileImg) {
        checkUserToken(dto.getUsername());
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkUserMatch(user, dto.getUsername(), dto.getPassword());

        String profileDir = String.format("media/%d/", id);
        try {
            Files.createDirectories(Path.of(profileDir));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String originalFilename = profileImg.getOriginalFilename();
        String[] fileNameSplit = originalFilename.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        String profileFilename = "image." + extension;
        String profilePath = profileDir + profileFilename;

        try {
            profileImg.transferTo(Path.of(profilePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        user.updateProfileImg(String.format("/static/images/%d/%s", id, profileFilename));
        userRepository.save(user);
    }

    private void checkUserToken(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getName().equals(username))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private void checkUserMatch(User user, String username, String password) {
        if (!user.getUsername().equals(username) || !passwordEncoder.matches(password, user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

}
