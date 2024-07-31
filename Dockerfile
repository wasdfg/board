FROM openjdk:17-jdk
COPY builds/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/app.jar"]