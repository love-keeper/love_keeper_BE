FROM --platform=linux/amd64 eclipse-temurin:17-jdk as builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM --platform=linux/amd64 eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Firebase 설정 파일 디렉토리 생성
RUN mkdir -p /app/config

# Add non-root user
RUN addgroup --system --gid 1001 appuser
RUN adduser --system --uid 1001 --gid 1001 --no-create-home appuser
USER appuser

EXPOSE 8082

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dserver.port=8082", "-jar", "/app/app.jar"]