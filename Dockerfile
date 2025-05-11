FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY src/main/resources/firebase-service-account.json /app/firebase-service-account.json
ENV FCM_CERTIFICATION_PATH=/app/firebase-service-account.json

# Default to port 8080 for dev, will be overridden by Spring profile
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
