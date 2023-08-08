package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.ResponseDto;
import com.likelion.sns.domain.dto.user.ResponseUserInfoDto;
import com.likelion.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class FriendController {

    private final UserService userService;

    // 친구 요청 목록 확인
    @GetMapping("/follows")
    public ResponseEntity<List<ResponseUserInfoDto>> getFriendList(Authentication authentication) {
        String username = authentication.getName();
        return  ResponseEntity.ok((userService.getFriendList(username)));
    }

    // 팔로우, 언팔로우
    @PostMapping("/follows/{userId}")
    public ResponseEntity<ResponseDto> follow(@PathVariable("userId") Long userId, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(ResponseDto.getMessage(userService.clickFollow(userId, username)));
    }

    // 팔로우 해제
    @DeleteMapping("/follows/{userId}")
    public ResponseEntity<ResponseDto> deleteMyFollow(@PathVariable("userId") Long userId, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(ResponseDto.getMessage(userService.unFollow(userId, username)));
    }

    // 친구 신청
    @PostMapping("/friends/{toUserId}")
    public ResponseEntity<ResponseDto> sendSuggestion(@PathVariable("toUserId") Long toUserId, Authentication authentication) {
        String username = authentication.getName();
        userService.sendFriend(toUserId, username);
        return ResponseEntity.ok(ResponseDto.getMessage("친구 요청을 보냈습니다."));
    }

    // 친구 수락
    @GetMapping("/friends/{fromUserId}")
    public ResponseEntity<ResponseDto> acceptSuggestion(@PathVariable("fromUserId") Long fromUserId, Authentication authentication) {
        String username = authentication.getName();
        userService.acceptFriend(fromUserId, username);
        return ResponseEntity.ok(ResponseDto.getMessage("친구 요청을 수락했습니다."));
    }

    // 친구 거절
    @DeleteMapping("/friends/{fromUserId}")
    public ResponseEntity<ResponseDto> rejectSuggestion(@PathVariable("fromUserId") Long fromUserId, Authentication authentication) {
        String username = authentication.getName();
        userService.rejectFriend(fromUserId, username);
        return ResponseEntity.ok(ResponseDto.getMessage("친구 요청을 거절했습니다."));
    }
}
