package com.kimje.chat.global.security;

import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker.Default;
import com.kimje.chat.user.entity.UserLogin;
import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserLoginRepository;
import com.kimje.chat.user.repository.UserRepository;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;
  private final UserLoginRepository userLoginRepository;
  private final PasswordEncoder passwordEncoder;

  public CustomOAuth2UserService(UserRepository userRepository, UserLoginRepository userLoginRepository , PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userLoginRepository = userLoginRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2Token token = userRequest.getAccessToken();
    System.out.println("액세스 토큰 : "  + token.getTokenValue());

    OidcIdToken idToken = null;
    if (userRequest instanceof OidcUserRequest oidcUserRequest) {
      idToken = oidcUserRequest.getIdToken();
      System.out.println("ID Token Claims: " + idToken.getClaims());
    }


    OAuth2User oAuth2User = super.loadUser(userRequest);
    String provider = userRequest.getClientRegistration().getRegistrationId();
    Map<String,Object> attributes = oAuth2User.getAttributes();
    OAuth2Response oAuth2Response = switch (provider){
      case "kakao" -> new KakaoResponse(oAuth2User.getAttributes());
      default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인");
    };

    String providerId = oAuth2Response.getProviderId();
    Users user = userRepository.findByEmail(oAuth2Response.getEmail()).orElse(null);

    if(user == null) {
      user = userRepository.save(Users.builder()
          .email(oAuth2Response.getEmail())
          .password(passwordEncoder.encode(UUID.randomUUID().toString()))
          .name(oAuth2Response.getName())
          .role(UserRole.ROLE_USER)
          .build());
    }

    // 8. user_logins에 provider 연결 정보 있는지 확인
    boolean exists = userLoginRepository.existsByUserUserIdAndLoginType(user.getUserId(), provider);
    if (!exists) {
      userLoginRepository.save(UserLogin.builder()
          .user(user)
          .loginType(provider)
          .providerId(oAuth2Response.getProviderId())
          .build());
    }

    return new CustomOAuth2User(user.getUserId(),user.getEmail(),user.getRole(),provider,providerId,attributes);
  }
}
