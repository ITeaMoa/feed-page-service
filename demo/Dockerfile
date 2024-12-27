# Use an official OpenJDK runtime (Java 17) as the base image for running the Spring Boot app
# FROM openjdk:17-jdk-slim - 429 Too Many Requests error 때문에 pull resource ecr로 바뀜
FROM public.ecr.aws/docker/library/openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

ARG AWS_DEFAULT_REGION
ARG AWS_TABLE
ARG AWS_ACCESS_KEY
ARG AWS_SECRET_KEY

# Set environment variables
ENV AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION}
ENV AWS_TABLE=${AWS_TABLE}
ENV AWS_ACCESS_KEY=${AWS_ACCESS_KEY}
ENV AWS_SECRET_KEY=${AWS_SECRET_KEY}

RUN echo "AWS_TABLE is: ${AWS_TABLE}"

# Set a working directory in the container
WORKDIR /app

# 애플리케이션 jar 파일을 컨테이너로 복사
COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application will run on
EXPOSE 8080

# 애플리케이션 실행 명령어 설정
ENTRYPOINT ["java", "-jar", "app.jar"]