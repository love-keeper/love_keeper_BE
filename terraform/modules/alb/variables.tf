variable "vpc_id" {
  description = "The ID of the VPC"
  type        = string
}

variable "public_subnet_ids" {
  description = "The IDs of the public subnets"
  type        = list(string)
}

variable "alb_sg_id" {
  description = "The ID of the ALB security group"
  type        = string
}

variable "project_name" {
  description = "The name of the project"
  type        = string
}

variable "environment" {
  description = "The deployment environment"
  type        = string
}

# Uncomment when ready for HTTPS
# variable "certificate_arn" {
#   description = "The ARN of the SSL certificate"
#   type        = string
# }