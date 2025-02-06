FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/my-app-1.0.0.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
