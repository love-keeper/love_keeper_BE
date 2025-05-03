FROM --platform=linux/amd64 eclipse-temurin:17-jdk as builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM --platform=linux/amd64 eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

COPY application.jar application.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dserver.port=8082", "-jar", "application.jar"]