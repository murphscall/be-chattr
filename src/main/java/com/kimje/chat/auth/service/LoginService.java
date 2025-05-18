package com.kimje.chat.auth.service;

import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.global.security.CustomUserDetails;
import com.kimje.chat.global.security.JwtTokenProvider;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.dto.UserResponseDTO.Info;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate stringRedisTemplate;
    private final TokenService tokenService;
    private final UserService userService;


    public UserResponseDTO.Info login(LoginDTO.Request request , HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
            )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserRole role = userDetails.getRole();
        // 여기 까지 user 로그인 정보 확인

        // user 정보로 accessToken 생성
        tokenService.createAccessToken(userDetails.getUserId() ,role , response);
        // refresh 토큰 생성 후 쿠키에 설정
        tokenService.createAndSaveRefreshToken(userDetails.getUserId() , response);
        // user 정보 가져오기

        // 유저 정보 반환
        return userService.getUserInfo(userDetails.getUserId());
    }



}
