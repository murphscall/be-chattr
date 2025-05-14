package com.kimje.chat.user.service;

import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.global.exception.EmailNotVerificationException;
import com.kimje.chat.global.security.AuthUser;
import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.entity.UserLogin;
import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;


    public void createUser(UserRequestDTO.Create dto) {

        String verificationStatus = redisTemplate.opsForValue().get(dto.getEmail());
        if(verificationStatus == null || !verificationStatus.equals("true")) {
            throw new EmailNotVerificationException("이메일 인증을 완료하지 않았거나 인증이 만료되었습니다.");
        }

        userRepository
            .findByEmail(dto.getEmail())
            .ifPresent(user ->{
                throw new IllegalStateException("중복된 이메일 입니다.");
            });

        Users user = dto.toEntity();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        UserLogin userLogin = UserLogin.builder()
            .loginType("NORMAL")
            .providerId(dto.getEmail())
            .user(user)
            .build();

        user.getUserLogins().add(userLogin);
        userRepository.save(user);
        redisTemplate.delete(dto.getEmail());
    }

    public void updateUser(UserRequestDTO.Update dto) {

    }

    public void deleteUser(UserRequestDTO.Delete dto) {

    }

    public UserResponseDTO.Info getUserInfo(AuthUser authUser) {
        Users user = userRepository.findById(authUser.getUserId())
            .orElseThrow(() -> new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

        return UserResponseDTO.Info.builder()
            .email(user.getEmail())
            .name(user.getName())
            .phone(user.getPhone())
            .createdAt(user.getCreatedAt())
            .build();

    }
}
