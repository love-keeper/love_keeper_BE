FROM --platform=linux/amd64 eclipse-temurin:17-jdk as builder
WORKDIR /app

# Copy Gradle files first for better layer caching
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build application
RUN ./gradlew bootJar --no-daemon

FROM --platform=linux/amd64 eclipse-temurin:17-jre
WORKDIR /app

# Install AWS CLI for KMS decryption (slim version)
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl unzip && \
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install && \
    rm -rf awscliv2.zip aws && \
    apt-get purge -y curl unzip && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create necessary directories
RUN mkdir -p /app/config

# Copy application jar from builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Copy Firebase configuration and decrypt script
COPY src/main/resources/firebase-service-account.json.encrypted /app/config/
COPY terraform/scripts/decrypt_firebase.sh /app/
RUN chmod +x /app/decrypt_firebase.sh

# Add non-root user for security
RUN addgroup --system --gid 1001 appuser && \
    adduser --system --uid 1001 --gid 1001 --no-create-home appuser

# Set proper permissions
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8082

# Healthcheck to ensure application is running properly
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run the decrypt script before starting the application
ENTRYPOINT ["/bin/sh", "-c", "/app/decrypt_firebase.sh && java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev} -Dfile.encoding=UTF-8 -Dserver.port=8082 -jar /app/app.jar"]
