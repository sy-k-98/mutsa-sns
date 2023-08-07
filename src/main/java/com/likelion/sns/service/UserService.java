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

    public void updateProfileImage(MultipartFile profileImg, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkUserMatch(user, username);

        String profileDir = String.format("media/%d/", user.getId());
        try {
            Files.createDirectories(Path.of(profileDir));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String originalFilename = profileImg.getOriginalFilename();
        String[] fileNameSplit = originalFilename.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        String profileFilename = "profileImg_" + user.getId() + "." +extension;
        String profilePath = profileDir + profileFilename;

        try {
            profileImg.transferTo(Path.of(profilePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        user.updateProfileImg(String.format("/static/profile/%d/%s", user.getId(), profileFilename));
        userRepository.save(user);
    }

    private void checkUserMatch(User user, String username) {
        if (!user.getUsername().equals(username))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

}
