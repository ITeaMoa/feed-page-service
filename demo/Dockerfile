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

# Copy the Gradle build files to the container
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY src /app/src

# Grant execution rights to the Gradle wrapper
RUN chmod +x ./gradlew

# Build the application JAR using Gradle
RUN ./gradlew build

# Expose the port that Spring Boot uses (default is 8080)
# will change after eks cluster added
EXPOSE 8080 

# Copy the generated JAR file into the container
COPY build/libs/mainpage-0.0.1-SNAPSHOT.jar /app/mainpage.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/mainpage.jar"]