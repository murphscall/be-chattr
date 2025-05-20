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
- **채팅방 -** CRUD 기능, 블랙리스트 생성 및 해제 , 권한 위임 및 부여 , 좋아요 , 오픈 프로필
- **사용자 -** Security 회원가입 및 로그인, OAuth 2.0 구글, 네이버 로그인, 회원정보 수정, 회원가입시 유효성 검사 및 중복 검사
- **메시지 -** CRUD 기능 , 좋아요 

### 3. 사용 기술

#### 3-1 백엔드

##### 주요 프레임워크 / 라이브러리
- Java 17
- SpringBoot 3.4.5
- JPA(Spring Data JPA)
- Spring Security
- OAuth 2.0

##### Build Tool
- Gradle 8.10.2

##### DataBase
- MySQL 8.0.4

## 3-2 프론트엔드
- React (vite)


## 구조 및 설계

### 1. 패키지 구조

<details>

<summary>패키지 구조 보기</summary>   


```
+---main
|   +---generated
|   +---java
|   |   \---com
|   |       \---kimje
|   |           \---chat
|   |               |   BackendApplication.java
|   |               |
|   |               +---common
|   |               |   \---response
|   |               |           ApiResponse.java
|   |               |
|   |               +---emailauth
|   |               |   +---entity
|   |               |   \---repository
|   |               \---user
|   |                   +---controller
|   |                   |       UserController.java
|   |                   |
|   |                   +---dto
|   |                   |       UserRequestDTO.java
|   |                   |
|   |                   +---entity
|   |                   |       Users.java
|   |                   |
|   |                   +---enums
|   |                   |       UserRole.java
|   |                   |
|   |                   +---repository
|   |                   |       UserRepository.java
|   |                   |
|   |                   \---service
|   |                           UserService.java
|   |
|   \---resources
|           application.properties
|
\---test
    \---java
        \---com
            \---kimje
                \---chat
                    |   BackendApplicationTests.java
                    |
                    \---user
                        \---service
                                UserServiceTest.java
 ```

 </details>   
 <br/>    

### 2. DB 설계
![Image](https://github.com/user-attachments/assets/7f71e4f1-94dc-48b9-a1f8-2569e4e9cc31)

### 3. API 설계
![Image](https://github.com/user-attachments/assets/151c1277-5bc9-4b1f-9551-71850fac443d)
![Image](https://github.com/user-attachments/assets/a7cd1092-059e-448e-a824-9cd5498cf84c)
![Image](https://github.com/user-attachments/assets/d254d417-a730-49b1-8417-55e50353bf3d)
![Image](https://github.com/user-attachments/assets/238bc595-f7fd-41cd-9da0-5902d05b3859)
![Image](https://github.com/user-attachments/assets/34d5c50a-165a-4c81-a68c-bd6848385be9)
![Image](https://github.com/user-attachments/assets/b0eceec6-9a2d-435e-b98d-69dcad06866f)
