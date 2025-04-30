FROM amazoncorretto:17-alpine

WORKDIR /app

# JAR 파일 경로를 명확하게 지정
COPY application.jar application.jar

# 컨테이너가 8082 포트를 사용함을 명시
EXPOSE 8082

# 환경 변수에서 포트를 가져오되, 기본값으로 8082 사용
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dserver.port=${PORT:8082}", "-jar", "application.jar"]