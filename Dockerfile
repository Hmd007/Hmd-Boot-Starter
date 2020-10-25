FROM openjdk:8-jdk-alpine
LABEL email = "wanashamad123@gmail.com"
RUN addgroup -S devuser && adduser -S hamad007 -G devuser
USER hamad007:devuser
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev-docker", "-jar", "/app.jar"]