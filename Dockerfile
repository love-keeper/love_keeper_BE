FROM amazoncorretto:17-alpine

WORKDIR /app

COPY application.jar application.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dserver.port=8082", "-jar", "application.jar"]