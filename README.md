## 목차
- [들어가며](#들어가며)
    - [프로젝트 소개](#1-프로젝트-소개)
    - [프로젝트 기능](#2-프로젝트-기능)
    - [사용 기술](#3-사용-기술)
        - [백엔드](#3-1-백엔드)
        - [프론트엔드](#3-2-프론트엔드)

- [구조 및 설계](#구조-및-설계)
    - [패키지 구조](#1-패키지-구조)
    - [DB 설계](#2-db-설계)
    - [API 설계](#3-api-설계)

- [개발 내용](#개발-내용)

- [마치며](#마치며)
    - [프로젝트 보완사항](#1-프로젝트-보완사항)
    - [후기](#2-후기)

## 들어가며
### 1. 프로젝트 소개

실시간 채팅 기능은 웹 개발의 기본 소양 중 하나로, 이를 직접 구현해보며 웹소켓, 인증, 메시지 처리 등의 흐름을 익히고자 현재 진행중인 프로젝트 입니다.
또한 이전 Petory 프로젝트를 통해서 아쉬웠던 일반회원과 소셜회원의 통합관리를 보완하고 사용하지 못했던 Redis의 캐싱 기술을 시도하려고 합니다.



### 2. 프로젝트 기능

프로젝트의 주요 기능은 다음과 같습니다.
- **채팅방 -** 채팅방 CRUD , 권한 부여 및 위임
- **사용자 -** Security 회원가입 및 로그인, OAuth 2.0 구글, 네이버 로그인, 회원정보 수정, 회원가입시 유효성 검사 및 중복 검사
- **메시지 -** 메시지 CRUD , 대상 태그 기능  

### 3. 사용 기술

#### 3-1 백엔드

##### 주요 프레임워크 / 라이브러리
- Java 17
- SpringBoot 3.4.5
- JPA(Spring Data JPA)
- Spring Security
- OAuth 2.0
- Redis 

##### Build Tool
- Gradle 8.10.2

##### DataBase
- MySQL 8.0.4

## 구조 및 설계

### 1. 패키지 구조
<details>
<summary>📁 패키지 구조 보기</summary>

```
└─📁 com
    └─📁 kimje
        └─📁 chat
            │  📄 BackendApplication.java
            │
            ├─📁 admin
            │  ├─📁 controller
            │  │      📄 AdminController.java
            │  │
            │  ├─📁 repository
            │  └─📁 service
            │
            ├─📁 auth
            │  ├─📁 controller
            │  │      📄 AuthController.java
            │  │
            │  ├─📁 dto
            │  │      📄 LoginDTO.java
            │  │
            │  ├─📁 repository
            │  └─📁 service
            │          📄 AuthService.java
            │          📄 TokenService.java
            │
            ├─📁 chats
            │  ├─📁 controller
            │  │      📄 ChatAdminController.java
            │  │      📄 ChatController.java
            │  │      📄 ChatMessageController.java
            │  │      📄 ChatUserController.java
            │  │      📄 MessageControler.java
            │  │
            │  ├─📁 dto
            │  │      📄 ChatRequestDTO.java
            │  │      📄 ChatResponseDTO.java
            │  │      📄 MessageRequestDTO.java
            │  │      📄 MessageResponseDTO.java
            │  │
            │  ├─📁 entity
            │  │      📄 Chat.java
            │  │      📄 ChatUser.java
            │  │      📄 Message.java
            │  │      📄 MessageLike.java
            │  │      📄 MessageMention.java
            │  │      📄 Notification.java
            │  │
            │  ├─📁 enums
            │  │      📄 ChatRole.java
            │  │      📄 ChatTopic.java
            │  │      📄 ChatType.java
            │  │      📄 MessageType.java
            │  │      📄 NotificationType.java
            │  │
            │  ├─📁 repository
            │  │      📄 ChatRepository.java
            │  │      📄 ChatUserRepository.java
            │  │      📄 MessageRepository.java
            │  │
            │  └─📁 service
            │          📄 ChatAdminService.java
            │          📄 ChatService.java
            │          📄 ChatUserService.java
            │
            ├─📁 emailauth
            │  ├─📁 controller
            │  │      📄 EmailController.java
            │  │
            │  ├─📁 dto
            │  │      📄 EmailRequestDTO.java
            │  │
            │  ├─📁 entity
            │  ├─📁 repository
            │  └─📁 service
            │          📄 EmailService.java
            │
            ├─📁 global
            │  ├─📁 config
            │  │      📄 OpenApiConfig.java
            │  │      📄 RedisConfig.java
            │  │      📄 SecurityConfig.java
            │  │      📄 WebSocketConfig.java
            │  │
            │  ├─📁 exception
            │  │  ├─📁 customexception
            │  │  │      📄 EmailNotVerificationException.java
            │  │  │      📄 FieldErrorException.java
            │  │  │      📄 InvalidVerificationCodeException.java
            │  │  │      📄 JwtInvalidTokenException.java
            │  │  │      📄 JwtTokenExpiredException.java
            │  │  │      📄 VerificationCodeExpiredException.java
            │  │  │
            │  │  └─📁 exhandler
            │  │          📄 GlobalExceptionHandler.java
            │  │
            │  ├─📁 redis
            │  │      📄 RedisService.java
            │  │
            │  ├─📁 response
            │  │      📄 ApiResponse.java
            │  │
            │  ├─📁 security
            │  │      📄 CustomUserDetails.java
            │  │      📄 CustomUserDetailsService.java
            │  │
            │  │  ├─📁 jwt
            │  │  │      📄 JwtAuthenticationEntryPoint.java
            │  │  │      📄 JwtAuthenticationFilter.java
            │  │  │      📄 JwtHandshakeInterceptor.java
            │  │  │      📄 JwtTokenProvider.java
            │  │  │
            │  │  └─📁 OAuth2
            │  │          📄 AuthUser.java
            │  │          📄 CustomOAuth2User.java
            │  │          📄 CustomOAuth2UserService.java
            │  │          📄 GoogleResponse.java
            │  │          📄 KakaoResponse.java
            │  │          📄 OAuth2LoginSuccessHandler.java
            │  │          📄 OAuth2Response.java
            │  │
            │  └─📁 util
            │          📄 CookieUtil.java
            │          📄 EmailVerifyPassGenerator.java
            │
            └─📁 user
                ├─📁 controller
                │      📄 UserController.java
                │
                ├─📁 dto
                │      📄 UserRequestDTO.java
                │      📄 UserResponseDTO.java
                │
                ├─📁 entity
                │      📄 User.java
                │      📄 UserLogin.java
                │
                ├─📁 enums
                │      📄 UserRole.java
                │
                ├─📁 repository
                │      📄 UserLoginRepository.java
                │      📄 UserRepository.java
                │
                └─📁 service
                        📄 UserService.java

```


</details>
<br/>    

### 2. DB 설계
#### DB 전체 구조
<img width="912" alt="Image" src="https://github.com/user-attachments/assets/914b7f40-736d-45d4-a389-5ea6b3b76bb8" />

#### 회원
<img width="894" alt="Image" src="https://github.com/user-attachments/assets/8890feda-a173-449f-91ca-f9ff8698b752" />

### 메시지
<img width="1033" alt="Image" src="https://github.com/user-attachments/assets/1cfbc3f8-bb46-4145-bab0-e94e5d88b517" />

### 채팅방
<img width="519" alt="Image" src="https://github.com/user-attachments/assets/001082e1-9ae7-4150-9ca2-e1999a45bc06" />



### 3. API 설계




### 4.사용자 인증 발급 흐름도

![Image](https://github.com/user-attachments/assets/fbc54f68-8736-4542-9b55-5c9da3adde79)

### 5. 시큐리티 구조 및 JWT 처리 흐름도
![Image](https://github.com/user-attachments/assets/d059a297-e45c-4336-ac2c-70cf78708428)