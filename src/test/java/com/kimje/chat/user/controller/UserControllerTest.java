package com.kimje.chat.user.controller;


import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

import com.kimje.chat.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;  // 컨트롤러가 의존하는 빈은 Mock 처리

  @Test
  void create_success() throws Exception {

    String jsonRequest = """
            {
                "email": "rlawlsgn22@gmail.com",
                "password": "sy8583lk^^",
                "name": "김돌돌",
                "phone": "010-2212-9624"
            }
            """;

    mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
        .andExpect(status().isCreated());
  }

  @Test
  void create_fail() throws Exception {
    String jsonRequest = """
            {
                "email": "rlawlsgn22@gmail.com",
                "password": "sy8583",
                "name": "홍길동",
                "phone": "010-2212-9624"
            }
            """;

    mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.data.password").exists());
  }
}