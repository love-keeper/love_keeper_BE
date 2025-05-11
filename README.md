# LoveKeeper

A Spring Boot application for [brief description of your application].

## Architecture

This project uses a multi-environment AWS architecture:

![Architecture Diagram](architecture-diagram.png)

Key components:
- Spring Boot backend API
- MySQL databases for persistence
- Redis for caching
- S3 for file storage
- Firebase Cloud Messaging for notifications
- AWS ECS for container orchestration
- Application Load Balancer for traffic routing
- GitHub Actions for CI/CD

## Development Environment

- Java 17
- Spring Boot
- Gradle
- MySQL
- Redis

## Project Structure

- `src/main/java`: Java source code
- `src/main/resources`: Configuration files and resources
- `terraform`: Infrastructure as Code using Terraform
- `.github/workflows`: CI/CD workflows using GitHub Actions

## Configuration Profiles

- `application.yml`: Common configuration
- `application-dev.yml`: Development environment configuration
- `application-prod.yml`: Production environment configuration

## Infrastructure Setup

The infrastructure is managed using Terraform. See [terraform/README.md](terraform/README.md) for details.

## Deployment

The application is deployed using GitHub Actions workflows:

- `deploy-dev.yml`: Deploys to the development environment from the `develop` branch
- `deploy-prod.yml`: Deploys to the production environment from the `main` branch

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/lovekeeper.git
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run locally:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

4. For infrastructure setup, see [terraform/README.md](terraform/README.md).