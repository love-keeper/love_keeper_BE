# LoveKeeper

## Architecture

The application is deployed on AWS with the following architecture:

- VPC with 4 subnets (2 public, 2 private)
- ECS Clusters for Dev and Release environments
- RDS MySQL instances for each environment
- ElastiCache Redis instances for each environment
- Application Load Balancer for routing traffic
- S3 for file storage
- Parameter Store for secrets and configuration

## Development Environment

### Prerequisites

- Java 17
- Docker
- AWS CLI
- Terraform (optional, for infrastructure provisioning)

### Local Development

1. Clone the repository
2. Create an `application-local.yml` file based on the existing profiles
3. Run the application locally with the `local` profile

```shell
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Deployment

### Infrastructure Setup

The infrastructure is managed with Terraform. To provision:

1. Navigate to the `terraform` directory
2. Copy `terraform.tfvars.example` to `terraform.tfvars` and fill in your values
3. Initialize Terraform and apply the configuration

```shell
terraform init
terraform plan
terraform apply
```

### CI/CD Pipeline

The application uses GitHub Actions for CI/CD:

- Pushing to the `develop` branch will build and deploy to the dev environment
- Pushing to the `main` branch will build and deploy to the release environment

Required GitHub Secrets:

- `AWS_ROLE_ARN`: ARN of the IAM role for GitHub Actions

## Configuration

Environment variables are managed through AWS Parameter Store:

- Dev environment: `/lovekeeper/dev/*`
- Release environment: `/lovekeeper/prod/*`

## Monitoring

The application uses Spring Boot Actuator for monitoring, which is available at:

- Dev: `https://dev.example.com/actuator`
- Release: `https://example.com/actuator`
