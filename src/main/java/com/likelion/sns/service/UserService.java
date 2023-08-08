package com.likelion.sns.service;

import com.likelion.sns.domain.dto.user.RequestUserLoginDto;
import com.likelion.sns.domain.dto.user.ResponseUserInfoDto;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.repository.UserRepository;
import jdk.jfr.Frequency;
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
import java.util.ArrayList;
import java.util.List;

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
        String profileFilename = "profileImg_" + user.getId() + "." + extension;
        String profilePath = profileDir + profileFilename;

        try {
            profileImg.transferTo(Path.of(profilePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        user.updateProfileImg(String.format("/static/profile/%d/%s", user.getId(), profileFilename));
        userRepository.save(user);
    }

    public ResponseUserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseUserInfoDto.fromEntity(user);
    }

    // 팔로우, 언팔로우
    public String clickFollow(Long userId, String username) {
        // 나
        User me = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // 상대
        User following = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (following == me)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        else {
            if (me.getFollowing().contains(following)) {
                me.getFollowing().remove(following);
                userRepository.save(me);
                return "팔로우가 취소되었습니다.";
            } else {
                me.getFollowing().add(following);
                userRepository.save(me);
                return "팔로우를 했습니다.";
            }
        }
    }

    // 나의 팔로워 삭제
    public String unFollow(Long userId, String username) {
        User me = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User follower = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (me.getFollowers().contains(follower)) {
            follower.getFollowing().remove(me);
            userRepository.save(follower);
            return "팔로우를 취소했습니다.";
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // 친구 요청
    public void sendFriend(Long userId, String username) {
        User fromUser = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User toUser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (fromUser == toUser)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        else {
            if (fromUser.getToUser().contains(toUser) || toUser.getToUser().contains(fromUser))
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            else {
                toUser.getFromUser().add(fromUser);
                userRepository.save(toUser);
            }
        }
    }

    // 맞팔 요청 확인
    public List<ResponseUserInfoDto> getFriendList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<ResponseUserInfoDto> friends = new ArrayList<>();
        for (User suggestion : user.getFromUser()) {
            if (!suggestion.getFromUser().contains(user))
                friends.add(ResponseUserInfoDto.fromEntity(suggestion));
        }
        return friends;
    }

    public void acceptFriend(Long userId, String username) {
        User me = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User suggestion = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (me.getFromUser().contains(suggestion)) {
            if (me.getToUser().contains(suggestion))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            else {
                suggestion.getFromUser().add(me);
                userRepository.save(suggestion);
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public void rejectFriend(Long userId, String username) {
        User me = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User suggestion = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (me.getFromUser().contains(suggestion)) {
            if (me.getToUser().contains(suggestion))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            else {
                me.getFromUser().remove(suggestion);
                userRepository.save(me);
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private void checkUserMatch(User user, String username) {
        if (!user.getUsername().equals(username))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

}
