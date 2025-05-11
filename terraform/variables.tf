variable "aws_region" {
  description = "The AWS region to deploy resources to"
  type        = string
  default     = "ap-northeast-2"
}

variable "project_name" {
  description = "The name of the project"
  type        = string
  default     = "lovekeeper"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_1_cidr" {
  description = "CIDR block for the first public subnet"
  type        = string
  default     = "10.0.1.0/24"
}

variable "public_subnet_2_cidr" {
  description = "CIDR block for the second public subnet"
  type        = string
  default     = "10.0.2.0/24"
}

variable "private_subnet_dev_cidr" {
  description = "CIDR block for the private subnet (Dev)"
  type        = string
  default     = "10.0.3.0/24"
}

variable "private_subnet_release_cidr" {
  description = "CIDR block for the private subnet (Release)"
  type        = string
  default     = "10.0.4.0/24"
}

variable "domain_name" {
  description = "The domain name for the application"
  type        = string
  default     = "example.com"
}

variable "s3_bucket_name" {
  description = "The name of the S3 bucket for storing files"
  type        = string
  default     = "lovekeeper-files"
}

variable "db_username" {
  description = "The username for the database"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "The password for the database"
  type        = string
  sensitive   = true
}

variable "mail_username" {
  description = "The username for the email service"
  type        = string
  sensitive   = true
}

variable "mail_password" {
  description = "The password for the email service"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "The secret for JWT token generation"
  type        = string
  sensitive   = true
}

variable "aws_access_key" {
  description = "AWS access key for application usage"
  type        = string
  sensitive   = true
}

variable "aws_secret_key" {
  description = "AWS secret key for application usage"
  type        = string
  sensitive   = true
}

variable "bastion_ami" {
  description = "The AMI ID for the bastion host"
  type        = string
  default     = "ami-0c9c942bd7bf113a2" # Amazon Linux 2023 (x86)
}

variable "key_name" {
  description = "The name of the key pair for SSH access"
  type        = string
  default     = "lovekeeper-key"
}

variable "allowed_ip_ranges" {
  description = "IP ranges allowed to access the bastion host"
  type        = list(string)
  default     = ["0.0.0.0/0"] # Ideally limit to specific IPs for security
}

variable "github_repo" {
  description = "GitHub repository path (org/repo)"
  type        = string
  default     = "yourusername/lovekeeper"
}
