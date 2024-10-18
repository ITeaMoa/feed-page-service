# 베이스 이미지 설정
FROM openjdk:17-jdk-alpine

# 애플리케이션 jar 파일을 컨테이너로 복사
COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행 명령어 설정
ENTRYPOINT ["java", "-jar", "/app.jar"]
