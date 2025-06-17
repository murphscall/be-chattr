package com.kimje.chat.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.kimje.chat.emailauth.exception.EmailNotVerificationException;
import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.exception.DuplicateResourceException;
import com.kimje.chat.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private RedisService redisService;

  @Test
  @DisplayName("회원 생성 성공")
  public void createUser_Success(){
    //given
    UserRequestDTO.Create dto = createUserDTO();

    when(redisService.get("test@example.com")).thenReturn("true");
    when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
    assertDoesNotThrow(() -> userService.createUser(dto));

    verify(userRepository).save(any(User.class));
    verify(redisService).delete("test@example.com");

  }

  @Test
  @DisplayName("이메일 인증 안됨 예외발생")
  public void createUser_EmailNotVerified_ThrowsException() {

    // given
      UserRequestDTO.Create dto = createUserDTO();
      when(redisService.get("test@example.com")).thenReturn(null);

    // when  & then
    EmailNotVerificationException exception = assertThrows(EmailNotVerificationException.class,
            () -> userService.createUser(dto));

    // save
    // 저장 중 발생한 예외가 EmailNotVerificationException 과 같음
    assertThat(exception.getClass()).isEqualTo(EmailNotVerificationException.class);
    verify(userRepository, never()).save(any(User.class));
  }

  private UserRequestDTO.Create createUserDTO() {
    return UserRequestDTO.Create.builder()
            .email("test@example.com")
            .password("password123")
            .phone("010-2231-5564")
            .name("김철수")
            .build();
  }


  @Test
  @DisplayName("이메일 중복 예외 발생")
  public void createUser_DuplicateEmail_ThrowsException() {
    UserRequestDTO.Create dto = createUserDTO();
    User existingUser = new User();

    when(redisService.get("test@example.com")).thenReturn("true");
    when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(existingUser));

    assertThrows(DuplicateResourceException.class, () -> userService.createUser(dto));
    verify(userRepository, never()).save(any(User.class));

  }

  @Test
  public void deleteUserTest(){
    long userId = 1L;
    UserRequestDTO.Delete dto = UserRequestDTO.Delete.builder()
        .password("sy8583lk^^")
        .build();
    User user = new User();
    user.setId(1L);
    user.setPassword(dto.getPassword());
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    when(passwordEncoder.matches(dto.getPassword(),user.getPassword())).thenReturn(true);

    userService.deleteUser(dto,userId);

    verify(userRepository).delete(any(User.class));

  }

}