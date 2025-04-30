FROM amazoncorretto:17-alpine

WORKDIR /app

# JAR 파일 경로를 명확하게 지정
COPY application.jar application.jar

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "application.jar"]