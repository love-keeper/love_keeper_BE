FROM amazoncorretto:17-alpine

WORKDIR /app

COPY ./build/libs/*.jar application.jar

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "application.jar"]
