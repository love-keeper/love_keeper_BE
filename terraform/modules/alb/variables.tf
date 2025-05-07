variable "environment" {
  description = "Environment (dev or release)"
  type        = string
}

variable "vpc_id" {
  description = "ID of the VPC"
  type        = string
}

variable "public_subnet_1_id" {
  description = "ID of public subnet 1"
  type        = string
}

variable "public_subnet_2_id" {
  description = "ID of public subnet 2"
  type        = string
}

variable "alb_security_group_id" {
  description = "ID of ALB security group"
  type        = string
}
