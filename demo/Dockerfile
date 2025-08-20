# Use an official OpenJDK runtime (Java 17) as the base image for running the Spring Boot app
# FROM openjdk:17-jdk-slim - 429 Too Many Requests error 때문에 pull resource ecr로 바뀜
FROM public.ecr.aws/docker/library/openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

ARG AWS_DEFAULT_REGION
ARG AWS_DYNAMODB_TABLE
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY

# Set environment variables
ENV AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION}
ENV AWS_DYNAMODB_TABLE=${AWS_DYNAMODB_TABLE}
ENV AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
ENV AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}

# 애플리케이션 jar 파일을 컨테이너로 복사
COPY build/libs/*-SNAPSHOT.jar /app/feedpage.jar

# Expose the port your application will run on
EXPOSE 8080

# 애플리케이션 실행 명령어 설정
ENTRYPOINT ["java", "-jar", "app/demo.jar"]