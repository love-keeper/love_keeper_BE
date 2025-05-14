FROM amazoncorretto:17

# 빌드된 JAR 파일 복사
COPY ./build/libs/*.jar app.jar

# 포트 노출 (Spring Boot 기본 포트)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]