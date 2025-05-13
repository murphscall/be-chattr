package com.kimje.chat.auth.service;

import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.global.security.CustomUserDetails;
import com.kimje.chat.global.security.JwtTokenProvider;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserRepository;
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

    public LoginService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginDTO.Response login(LoginDTO.Request request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
            )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserRole role = userDetails.getUser().getRole();
        String token = jwtTokenProvider.createToken(authentication.getName() , role);

        LoginDTO.Response response = new LoginDTO.Response();
        response.setAccessToken(token);
        return response;
    }

}
