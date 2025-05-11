package com.kimje.chat.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openApi() {
    Info info = new Info()
        .title("실시간 채팅 Chattr API Document")
        .version("1.0")
        .description("실시간 채팅 Chattr의 API 명세서 입니다.");
    return new OpenAPI()
        .components(new Components())
        .info(info);
  }
}
