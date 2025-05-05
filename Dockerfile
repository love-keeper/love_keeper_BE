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

# Copy the encrypted Firebase service account file
COPY src/main/resources/firebase-service-account.json.encrypted /app/config/

# Copy the decrypt script 
COPY terraform/scripts/decrypt_firebase.sh /app/
RUN chmod +x /app/decrypt_firebase.sh

# Install AWS CLI for KMS decryption
RUN apt-get update && \
    apt-get install -y curl unzip && \
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install && \
    rm -rf awscliv2.zip aws && \
    apt-get remove -y curl unzip && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Add non-root user
RUN addgroup --system --gid 1001 appuser
RUN adduser --system --uid 1001 --gid 1001 --no-create-home appuser
USER appuser

EXPOSE 8082

# Run the decrypt script before starting the application
ENTRYPOINT ["/bin/sh", "-c", "/app/decrypt_firebase.sh && java -Dfile.encoding=UTF-8 -Dserver.port=8082 -jar /app/app.jar"]