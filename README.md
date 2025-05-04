# Lovekeeper Infrastructure

This repository contains Terraform configuration for the Lovekeeper project infrastructure on AWS.

## Architecture

The infrastructure consists of:

- VPC with public and private subnets across two availability zones
- Application Load Balancer in public subnets
- NAT Gateways for outbound internet access from private subnets
- Bastion host for secure administrative access
- ECS Fargate clusters for dev and release environments
- RDS MySQL databases for dev and release environments
- ElastiCache Redis clusters for dev and release environments
- S3 bucket for file storage
- ECR repository for Docker images

## Prerequisites

- AWS CLI configured with appropriate credentials
- Terraform 1.0.0 or newer
- A public SSH key for the bastion host

## Setup Instructions

1. Create a `terraform.tfvars` file with your variable values:

```hcl
aws_region             = "ap-northeast-2"
db_username            = "admin"
db_password            = "your-secure-password"
fcm_api_key            = "your-fcm-api-key"
terraform_state_bucket = "your-terraform-state-bucket"
```

2. Initialize Terraform:

```bash
terraform init
```

3. Create SSH key for bastion host:

```bash
ssh-keygen -t rsa -b 4096 -f ./bastion_key -C "bastion@lovekeeper"
```

4. Plan the infrastructure:

```bash
terraform plan -out=tfplan
```

5. Apply the infrastructure:

```bash
terraform apply tfplan
```

## CI/CD Integration

This infrastructure supports a GitFlow-based CI/CD pipeline:

- Code pushed to the `develop` branch deploys to the dev environment
- Code pushed to the `main` branch deploys to the release environment

## Network Architecture

- VPC CIDR: 10.0.0.0/16
- Public Subnet 1: 10.0.1.0/24
- Public Subnet 2: 10.0.2.0/24
- Private Subnet 1 (Dev): 10.0.3.0/24
- Private Subnet 2 (Release): 10.0.4.0/24

## Monitoring and Logging

- ECS task logs are sent to CloudWatch
- Container insights are enabled for ECS clusters

## Security Notes

- Production deployments should restrict access to the bastion host to specific IPs
- ACM certificate should be properly configured for HTTPS
- CORS settings should be restricted to specific origins
