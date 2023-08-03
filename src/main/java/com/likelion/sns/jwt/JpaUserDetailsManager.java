package com.likelion.sns.jwt;

import com.likelion.sns.domain.dto.user.RequestUserLoginDto;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.jwt.CustomUserDetails;
import com.likelion.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;

    @Override
    public void createUser(UserDetails user) {
        if (this.userExists(user.getUsername())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        userRepository.save(((CustomUserDetails) user).newEntity());
    }

    @Override
    public void updateUser(UserDetails user) {
        User user1 = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user1.updateInfo((CustomUserDetails) user);
        userRepository.save(user1);
    }

    @Override
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userRepository.delete(user);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        User user = userRepository.findByPassword(oldPassword).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.updatePassword(newPassword);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user1 = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return CustomUserDetails.fromEntity(user1);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

}
