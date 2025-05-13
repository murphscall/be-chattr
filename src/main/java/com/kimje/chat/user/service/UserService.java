package com.kimje.chat.user.service;

import com.kimje.chat.global.exception.EmailNotVerificationException;
import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.entity.UserLogin;
import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    public void getUserInfo() {
        
    }
}
