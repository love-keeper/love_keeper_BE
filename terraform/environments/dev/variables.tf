variable "db_username" {
  description = "Database username for dev environment"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Database password for dev environment"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret key for dev environment"
  type        = string
  sensitive   = true
}

variable "mail_username" {
  description = "Mail username for dev environment"
  type        = string
  sensitive   = true
}

variable "mail_password" {
  description = "Mail password for dev environment"
  type        = string
  sensitive   = true
}

variable "aws_access_key" {
  description = "AWS access key for dev environment"
  type        = string
  sensitive   = true
}

variable "aws_secret_key" {
  description = "AWS secret key for dev environment"
  type        = string
  sensitive   = true
}

variable "s3_bucket_name" {
  description = "S3 bucket name for dev environment"
  type        = string
  default     = "lovekeeper-dev"
}