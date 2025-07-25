package com.kimje.chat.global.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.kimje.chat.global.security.jwt.JwtAuthenticationEntryPoint;
import com.kimje.chat.global.security.jwt.JwtAuthenticationFilter;
import com.kimje.chat.global.security.jwt.JwtTokenProvider;
import com.kimje.chat.global.security.OAuth2.OAuth2LoginSuccessHandler;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtTokenProvider jwtTokenProvider;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
		JwtTokenProvider jwtTokenProvider,
		JwtAuthenticationFilter jwtAuthenticationFilter,
		OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtTokenProvider = jwtTokenProvider;
		this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.cors(withDefaults())
			.csrf(csrf -> csrf.disable())
			.formLogin(formLogin -> formLogin.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.securityContext(security -> security
				.requireExplicitSave(false) // 또는 SecurityContextRepository를 NoOp으로 설정
			);

		http.exceptionHandling(exceptionHandling -> exceptionHandling
			.authenticationEntryPoint(jwtAuthenticationEntryPoint));

		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, "/api/users", "/api/auth/login", "/api/email/send", "/api/auth/refresh", "/api/auth/logout", "/api/auth/authentication",
					"/api/email/verify").permitAll()
				.requestMatchers("/").permitAll()
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.anyRequest().authenticated());

		http
			.oauth2Login(oauth2 -> oauth2
				.successHandler(oAuth2LoginSuccessHandler)
			);
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("frontend-url")); // 프론트 주소
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true); // 필요한 경우 true
		config.setExposedHeaders(List.of("Authorization")); // 토큰을 헤더로 받을 경우

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

}
