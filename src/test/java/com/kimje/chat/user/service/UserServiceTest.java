package com.kimje.chat.user.service;

import static org.junit.jupiter.api.Assertions.*;

import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  private UserService userService;
  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("유저 저장 성공")
  void createUser() {
    String email = "test@test.com";
    UserRequestDTO.Create dto = new UserRequestDTO.Create();
    dto.setEmail(email);
    dto.setPassword("123456");
    dto.setName("test");
    dto.setPhone("123456789");

    // when
    userService.createUser(dto);

    //then
    User findUser = userRepository.findByEmail(email).orElse(null);
    assertNotNull(findUser);
    assertEquals(email, findUser.getEmail());
    assertEquals("test", findUser.getName());

  }
}