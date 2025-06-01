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
```mermaid
sequenceDiagram
    autonumber
    participant User
    participant Frontend
    participant Backend
    participant Redis

    %% ✅ 초기 로그인 시
    rect rgb(216, 245, 216)
    note over User, Redis: ✅ 초기 로그인 시
    User->>Frontend: 로그인 요청 (email, password)
    Frontend->>Backend: POST /api/auth/login
    Backend->>Redis: RefreshToken(UUID) 저장
    Backend-->>Frontend: AccessToken(JWT), RefreshToken(UUID)
    Frontend-->>User: 쿠키 저장 (HTTP-only)
    end

    %% ✅ 보호된 API 요청 시
    rect rgb(224, 234, 255)
    note over User, Redis: ✅ 보호된 API 요청 시
    User->>Frontend: API 요청 (AccessToken 포함)
    Frontend->>Backend: GET /api/protected
    alt AccessToken 유효
        Backend-->>Frontend: 정상 응답
    else AccessToken 만료
        Backend-->>Frontend: 401 Unauthorized

        %% ✅ 리프레시 토큰으로 재발급
        note over Frontend, Redis: ✅ 리프레시 토큰으로 재발급 요청
        Frontend->>Backend: POST /api/auth/refresh (with RefreshToken 쿠키)
        Backend->>Redis: RefreshToken 조회 및 검증
        alt RefreshToken 유효
            Redis-->>Backend: OK
            Backend->>Redis: 기존 RefreshToken 삭제
            Backend->>Redis: 새로운 RefreshToken 저장
            Backend-->>Frontend: 새 AccessToken + 새 RefreshToken
        else RefreshToken 없음 또는 만료
            Backend-->>Frontend: 401 Unauthorized
        end
    end
    end

    %% ✅ 로그아웃 시
    rect rgb(255, 240, 214)
    note over User, Redis: ✅ 로그아웃 시
    User->>Frontend: 로그아웃 요청
    Frontend->>Backend: POST /api/auth/logout
    Backend->>Redis: RefreshToken 삭제
    Backend-->>Frontend: 쿠키 삭제 응답
    end
```

### 5. 시큐리티 구조 및 JWT 처리 흐름도
```mermaid
sequenceDiagram
autonumber
participant 사용자
participant UsernamePasswordAuthenticationFilter
participant AuthenticationManager
participant CustomUserDetailsService
participant JwtTokenProvider
participant DispatcherServlet
participant OAuth2LoginAuthenticationFilter
participant CustomOAuth2UserService
participant JwtAuthenticationFilter
participant @RestController
participant UserService
participant UserRepository
participant Database

    %% Form 로그인
    사용자->>UsernamePasswordAuthenticationFilter: POST /login (username, password)
    UsernamePasswordAuthenticationFilter->>AuthenticationManager: authenticate()
    AuthenticationManager->>CustomUserDetailsService: loadUserByUsername()
    CustomUserDetailsService-->>AuthenticationManager: CustomUserDetails
    AuthenticationManager-->>UsernamePasswordAuthenticationFilter: 인증 객체 반환
    UsernamePasswordAuthenticationFilter->>JwtTokenProvider: createToken()
    JwtTokenProvider-->>UsernamePasswordAuthenticationFilter: JWT 발급
    UsernamePasswordAuthenticationFilter-->>사용자: JWT 반환

    %% OAuth2 로그인
    사용자->>DispatcherServlet: GET /oauth2/authorization/kakao
    DispatcherServlet->>OAuth2LoginAuthenticationFilter: 인증 처리
    OAuth2LoginAuthenticationFilter->>CustomOAuth2UserService: loadUser()
    CustomOAuth2UserService->>UserRepository: findByEmail()
    UserRepository-->>CustomOAuth2UserService: Users
    CustomOAuth2UserService-->>OAuth2LoginAuthenticationFilter: CustomOAuth2User
    OAuth2LoginAuthenticationFilter->>JwtTokenProvider: createToken()
    JwtTokenProvider-->>OAuth2LoginAuthenticationFilter: JWT 발급
    OAuth2LoginAuthenticationFilter-->>사용자: JWT + 리다이렉트

    %% 토큰으로 사용자 정보 요청
    사용자->>JwtAuthenticationFilter: GET /api/users/me (Authorization: Bearer ...)
    JwtAuthenticationFilter->>JwtTokenProvider: validateToken()
    JwtTokenProvider-->>JwtAuthenticationFilter: true
    JwtAuthenticationFilter->>JwtTokenProvider: getAuthentication()
    JwtTokenProvider->>UserRepository: findByEmail()
    UserRepository-->>JwtTokenProvider: Users
    JwtTokenProvider-->>JwtAuthenticationFilter: Authentication(CustomUserDetails)
    JwtAuthenticationFilter-->>@RestController: SecurityContext 설정 완료 후 요청 전달
    @RestController->>UserService: getUserInfo()
    UserService->>UserRepository: findBy(id)
    UserRepository-->>UserService: Users
    UserService-->>@RestController: UserResponseDTO
    @RestController-->>사용자: JSON 응답 반환
```