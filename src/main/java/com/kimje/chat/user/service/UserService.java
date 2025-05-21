package com.kimje.chat.user.service;

import com.kimje.chat.global.exception.customexception.EmailNotVerificationException;
import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.entity.UserLogin;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RedisService redisService;

	public void createUser(UserRequestDTO.Create dto) {

		String verificationStatus = redisService.get(dto.getEmail());
		if (verificationStatus == null || !verificationStatus.equals("true")) {
			throw new EmailNotVerificationException("이메일 인증을 완료하지 않았거나 인증이 만료되었습니다.");
		}

		userRepository
			.findByEmail(dto.getEmail())
			.ifPresent(user -> {
				throw new IllegalStateException("중복된 이메일 입니다.");
			});

		User user = dto.toEntity();
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		UserLogin userLogin = UserLogin.builder()
			.loginType("NORMAL")
			.providerId(dto.getEmail())
			.user(user)
			.build();

		user.getUserLogins().add(userLogin);
		userRepository.save(user);
		redisService.delete(dto.getEmail());
	}

	public void updateUser(UserRequestDTO.Update dto) {

	}

	public void deleteUser(UserRequestDTO.Delete dto, long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("찾을 수 없는 회원입니다."));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
		}

		userRepository.delete(user);

	}

	public UserResponseDTO.Info getUserInfo(long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		return UserResponseDTO.Info.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.phone(user.getPhone())
			.createdAt(user.getCreatedAt())
			.build();

	}
}
