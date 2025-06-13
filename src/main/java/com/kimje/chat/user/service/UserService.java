package com.kimje.chat.user.service;

import com.kimje.chat.user.exception.DuplicateResourceException;
import com.kimje.chat.emailauth.exception.EmailNotVerificationException;
import com.kimje.chat.user.exception.UserNotFoundException;
import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.entity.UserLogin;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RedisService redisService;

	@Transactional
	public void createUser(UserRequestDTO.Create dto) {
		log.info("🟢[REGISTER] 이메일 검증 여부 검사 ={}" , dto.getEmail());
		String verificationStatus = redisService.get(dto.getEmail());
		if (verificationStatus == null || !verificationStatus.equals("true")) {
			throw new EmailNotVerificationException("이메일 인증을 완료하지 않았거나 인증이 만료되었습니다.");
		}
		log.info("🟢[REGISTER] 이메일 검증 완료 ={}" , dto.getEmail());
		log.info("🟢[REGISTER] 이메일 중복 검사 ={}" , dto.getEmail());
		userRepository
			.findByEmail(dto.getEmail())
			.ifPresent(user -> {
				throw new DuplicateResourceException("중복된 이메일 입니다.");
			});
		log.info("🟢[REGISTER] 이메일 중복 검사 완료 ={}" , dto.getEmail());
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
		log.info("🟢[REGISTER] 회원 저장 완료 ={}" , dto.getEmail());
	}

	public void updateUser(UserRequestDTO.Update dto) {

	}

	@Transactional
	public void deleteUser(UserRequestDTO.Delete dto, long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("찾을 수 없는 회원입니다.",userId));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
		}

		userRepository.delete(user);

	}

	@Transactional(readOnly = true)
	public UserResponseDTO.Info getUserInfo(long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("사용자 정보를 찾을 수 없습니다.",userId));

		return UserResponseDTO.Info.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.phone(user.getPhone())
			.createdAt(user.getCreatedAt())
			.build();

	}
}
