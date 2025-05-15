FROM amazoncorretto:17

# 작업 디렉터리 설정
WORKDIR /app

# 실행에 필요한 시스템 패키지 설치 (필요시)
# RUN yum update -y

# 애플리케이션 JAR 파일 복사
COPY ./build/libs/*.jar app.jar

# 애플리케이션 포트 노출
EXPOSE 8080

# 한국 시간대 설정 (선택사항)
ENV TZ=Asia/Seoul

# JVM 옵션 설정 (메모리 최적화)
ENV JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]