FROM openjdk:17-alpine

# build/libs/ 에 있는 jar 파일을 JAR_FILE 변수에 저장
ARG JAR_FILE=build/libs/chat-0.0.1-SNAPSHOT.jar

# JAR_FILE을 app.jar로 복사
COPY ${JAR_FILE} app.jar

# Docker 컨테이너가 시작될 때 /app.jar 실행 , 컨테이너가 시작될 때마다 실행함
# 애플리케이션 timezone을 대한민국으로 설정
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","/app.jar"]

