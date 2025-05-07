variable "environment" {
  description = "Environment (dev or release)"
  type        = string
}

variable "region" {
  description = "AWS region"
  type        = string
  default     = "ap-northeast-2"
}

variable "private_subnet_1_id" {
  description = "ID of private subnet 1"
  type        = string
}

variable "private_subnet_2_id" {
  description = "ID of private subnet 2"
  type        = string
}

variable "ecs_security_group_id" {
  description = "ID of ECS security group"
  type        = string
}

variable "target_group_arn" {
  description = "ARN of ALB target group"
  type        = string
}

variable "task_cpu" {
  description = "CPU units for the task (1 vCPU = 1024 CPU units)"
  type        = string
  default     = "256"
}

variable "task_memory" {
  description = "Memory for the task (in MiB)"
  type        = string
  default     = "512"
}

variable "service_desired_count" {
  description = "Desired count of task instances"
  type        = number
  default     = 1
}

variable "s3_bucket_name" {
  description = "Name of the S3 bucket"
  type        = string
}

variable "mail_username" {
  description = "Mail username"
  type        = string
  sensitive   = true
}

variable "mail_password" {
  description = "Mail password"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret"
  type        = string
  sensitive   = true
}

variable "aws_access_key" {
  description = "AWS access key"
  type        = string
  sensitive   = true
}

variable "aws_secret_key" {
  description = "AWS secret key"
  type        = string
  sensitive   = true
}
