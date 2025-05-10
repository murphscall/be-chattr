package com.kimje.chat.user.service;

import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.entity.UserLogin;
import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void createUser(UserRequestDTO.Create dto) {
        Users user = dto.toEntity();
        user.setRole(UserRole.ROLE_USER);

        UserLogin userLogin = new UserLogin();
        userLogin.setLoginType("NORMAL");
        userLogin.setProviderId(dto.getEmail());
        userLogin.setUser(user);

        user.getUserLogins().add(userLogin);
        userRepository.save(user);
    }

    public void updateUser(UserRequestDTO.Update dto) {

    }

    public void deleteUser(UserRequestDTO.Delete dto) {

    }
}
