package com.kimje.chat.auth.service;

import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.global.security.CustomUserDetails;
import com.kimje.chat.global.security.JwtTokenProvider;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserRepository;
import com.kimje.chat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public LoginService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
        UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    public LoginDTO.Response login(LoginDTO.Request request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
            )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserRole role = userDetails.getRole();
        String token = jwtTokenProvider.createToken(authentication.getName() , role);
        UserResponseDTO.Info userInfo = userService.getUserInfo(userDetails.getUserId());

        LoginDTO.Response response = new LoginDTO.Response();
        response.setAccessToken(token);
        response.setUserInfo(userInfo);
        return response;
    }

}
