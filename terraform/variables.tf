variable "aws_region" {
  description = "AWS region"
  default     = "ap-northeast-2"
}

variable "project_name" {
  description = "Project name used for resource naming"
  default     = "lovekeeper"
}

variable "environment" {
  description = "Environment (dev, prod)"
  default     = "dev-prod"
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  default     = "10.0.0.0/16"
}

variable "public_subnet_1_cidr" {
  description = "CIDR block for public subnet 1"
  default     = "10.0.1.0/24"
}

variable "public_subnet_2_cidr" {
  description = "CIDR block for public subnet 2"
  default     = "10.0.2.0/24"
}

variable "private_subnet_1_cidr" {
  description = "CIDR block for private subnet 1 (Dev)"
  default     = "10.0.3.0/24"
}

variable "private_subnet_2_cidr" {
  description = "CIDR block for private subnet 2 (Prod)"
  default     = "10.0.4.0/24"
}

variable "availability_zones" {
  description = "Availability zones"
  type        = list(string)
  default     = ["ap-northeast-2a", "ap-northeast-2c"]
}

variable "key_name" {
  description = "EC2 key pair name"
  default     = "lovekeeper-key"
}

variable "container_port" {
  description = "Container port"
  default     = 8080
}

# ECS Dev Variables
variable "ecs_task_cpu_dev" {
  description = "ECS task CPU units for dev"
  default     = 512
}

variable "ecs_task_memory_dev" {
  description = "ECS task memory for dev"
  default     = 1024
}

variable "ecs_task_count_dev" {
  description = "Number of ECS tasks for dev"
  default     = 1
}

# ECS Prod Variables
variable "ecs_task_cpu_prod" {
  description = "ECS task CPU units for prod"
  default     = 1024
}

variable "ecs_task_memory_prod" {
  description = "ECS task memory for prod"
  default     = 2048
}

variable "ecs_task_count_prod" {
  description = "Number of ECS tasks for prod"
  default     = 2
}

# RDS Dev Variables
variable "db_instance_class_dev" {
  description = "RDS instance class for dev"
  default     = "db.t3.small"
}

variable "db_allocated_storage_dev" {
  description = "RDS allocated storage for dev"
  default     = 20
}

variable "db_username_dev" {
  description = "RDS username for dev"
  sensitive   = true
  default     = "admin"
}

variable "db_password_dev" {
  description = "RDS password for dev"
  sensitive   = true
  default     = "change-me"
}

# RDS Prod Variables
variable "db_instance_class_prod" {
  description = "RDS instance class for prod"
  default     = "db.t3.medium"
}

variable "db_allocated_storage_prod" {
  description = "RDS allocated storage for prod"
  default     = 50
}

variable "db_username_prod" {
  description = "RDS username for prod"
  sensitive   = true
  default     = "admin"
}

variable "db_password_prod" {
  description = "RDS password for prod"
  sensitive   = true
  default     = "change-me"
}

# Redis Variables
variable "redis_node_type_dev" {
  description = "ElastiCache Redis node type for dev"
  default     = "cache.t3.small"
}

variable "redis_node_type_prod" {
  description = "ElastiCache Redis node type for prod"
  default     = "cache.t3.medium"
}

variable "redis_port" {
  description = "ElastiCache Redis port"
  default     = 6379
}

# Secret Variables
variable "jwt_secret" {
  description = "JWT secret key"
  sensitive   = true
  default     = "change-me"
}

variable "aws_access_key" {
  description = "AWS access key"
  sensitive   = true
  default     = "change-me"
}

variable "aws_secret_key" {
  description = "AWS secret key"
  sensitive   = true
  default     = "change-me"
}

variable "aws_s3_bucket" {
  description = "AWS S3 bucket name"
  default     = "lovekeeper-storage"
}

variable "mail_username" {
  description = "Mail username"
  sensitive   = true
  default     = "change-me"
}

variable "mail_password" {
  description = "Mail password"
  sensitive   = true
  default     = "change-me"
}