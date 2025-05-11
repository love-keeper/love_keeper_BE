variable "project_name" {
  description = "The name of the project"
  type        = string
}

variable "environment" {
  description = "The deployment environment"
  type        = string
}

variable "dev_db_username" {
  description = "Dev database username"
  type        = string
  sensitive   = true
}

variable "dev_db_password" {
  description = "Dev database password"
  type        = string
  sensitive   = true
}

variable "prod_db_username" {
  description = "Prod database username"
  type        = string
  sensitive   = true
}

variable "prod_db_password" {
  description = "Prod database password"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret key"
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

variable "aws_s3_bucket" {
  description = "AWS S3 bucket name"
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