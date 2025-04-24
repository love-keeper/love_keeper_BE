FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE ${PORT:-8080}
ENTRYPOINT ["java", "-Dspring.profiles.active=elb", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]