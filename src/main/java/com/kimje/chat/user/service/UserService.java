package com.kimje.chat.user.service;

import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.entity.UserLogin;
import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void createUser(UserRequestDTO.Create dto) {

        userRepository
            .findByEmail(dto.getEmail())
            .ifPresent(user ->{
                throw new IllegalStateException("중복된 이메일 입니다.");
            });

        Users user = dto.toEntity();
        user.setRole(UserRole.ROLE_USER);

        UserLogin userLogin = UserLogin.builder()
            .loginType("NORMAL")
            .providerId(dto.getEmail())
            .user(user)
            .build();

        user.getUserLogins().add(userLogin);
        userRepository.save(user);
    }

    public void updateUser(UserRequestDTO.Update dto) {

    }

    public void deleteUser(UserRequestDTO.Delete dto) {

    }
}
