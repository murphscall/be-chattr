package com.kimje.chat.user.service;

import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void createUser(UserRequestDTO.Create dto) {
        Users user = dto.toEntity();
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    public void updateUser(UserRequestDTO.Update dto) {

    }

    public void deleteUser(UserRequestDTO.Delete dto) {

    }
}
