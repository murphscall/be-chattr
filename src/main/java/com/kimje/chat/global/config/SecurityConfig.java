package com.kimje.chat.global.config;

import com.kimje.chat.global.security.CustomOAuth2UserService;
import com.kimje.chat.global.security.JwtAuthenticationEntryPoint;
import com.kimje.chat.global.security.JwtAuthenticationFilter;
import com.kimje.chat.global.security.JwtTokenProvider;
import com.kimje.chat.global.security.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtTokenProvider jwtTokenProvider;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

  public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
      JwtTokenProvider jwtTokenProvider,
      OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    this.jwtTokenProvider = jwtTokenProvider;
    this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
  }



  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .formLogin(formLogin -> formLogin.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.exceptionHandling(exceptionHandling -> exceptionHandling
        .authenticationEntryPoint(jwtAuthenticationEntryPoint));

    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST ,"/api/users" , "/api/auth/login", "/api/email/send" , "/api/email/verify").permitAll()
            .requestMatchers("/").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated());

    http
        .oauth2Login(oauth2 -> oauth2
        .successHandler(oAuth2LoginSuccessHandler)
    );
    http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);



    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
