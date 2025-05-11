# LoveKeeper Infrastructure

This directory contains Terraform configurations for deploying the LoveKeeper application infrastructure on AWS.

## Architecture

The infrastructure consists of:

- VPC with public and private subnets
- Internet Gateway and NAT Gateways
- Application Load Balancer
- ECS Clusters for Dev and Prod environments
- RDS MySQL databases for Dev and Prod
- ElastiCache Redis clusters for Dev and Prod
- S3 bucket for file storage
- ECR repository for Docker images
- Bastion host for secure database access
- Secrets Manager for sensitive information

## Prerequisites

- AWS CLI installed and configured
- Terraform CLI installed (v1.0+)
- AWS credentials with appropriate permissions

## Setup

1. Initialize Terraform:

```bash
terraform init
```

2. Create a `terraform.tfvars` file with your variables:

```hcl
# AWS Region
aws_region = "ap-northeast-2"

# Project Information
project_name = "lovekeeper"
environment  = "dev-prod"

# VPC and Subnets
vpc_cidr             = "10.0.0.0/16"
public_subnet_1_cidr = "10.0.1.0/24"
public_subnet_2_cidr = "10.0.2.0/24"
private_subnet_1_cidr = "10.0.3.0/24"
private_subnet_2_cidr = "10.0.4.0/24"
availability_zones   = ["ap-northeast-2a", "ap-northeast-2c"]

# EC2 Key Pair
key_name = "your-key-name"

# Database Credentials
db_username_dev  = "admin"
db_password_dev  = "your-secure-password"
db_username_prod = "admin"
db_password_prod = "your-secure-password"

# Secrets
jwt_secret     = "your-jwt-secret"
aws_access_key = "your-aws-access-key"
aws_secret_key = "your-aws-secret-key"
mail_username  = "your-mail-username"
mail_password  = "your-mail-password"
```

3. Plan the deployment:

```bash
terraform plan
```

4. Apply the configuration:

```bash
terraform apply
```

## Module Structure

- `vpc`: VPC, subnets, and network components
- `security`: Security groups for all resources
- `alb`: Application Load Balancer configuration
- `ecs`: ECS clusters, services, and tasks for both environments
- `rds`: RDS databases for both environments
- `redis`: ElastiCache Redis clusters for both environments
- `s3`: S3 buckets for file storage and Terraform state
- `ecr`: Container registry for Docker images
- `bastion`: Bastion host for secure database access
- `secrets`: Secrets Manager for sensitive information

## GitHub Actions CI/CD

The project includes GitHub Actions workflows for CI/CD:

- `deploy-dev.yml`: Deploys to the development environment when changes are pushed to the `develop` branch
- `deploy-prod.yml`: Deploys to the production environment when changes are pushed to the `main` branch

To use these workflows, add the following secrets to your GitHub repository:

- `AWS_ACCESS_KEY_ID`: Your AWS access key
- `AWS_SECRET_ACCESS_KEY`: Your AWS secret key