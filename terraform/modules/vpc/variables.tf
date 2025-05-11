variable "vpc_cidr" {
  description = "The CIDR block for the VPC"
  type        = string
}

variable "public_subnet_1_cidr" {
  description = "The CIDR block for public subnet 1"
  type        = string
}

variable "public_subnet_2_cidr" {
  description = "The CIDR block for public subnet 2"
  type        = string
}

variable "private_subnet_1_cidr" {
  description = "The CIDR block for private subnet 1 (Dev)"
  type        = string
}

variable "private_subnet_2_cidr" {
  description = "The CIDR block for private subnet 2 (Prod)"
  type        = string
}

variable "availability_zones" {
  description = "The availability zones to use"
  type        = list(string)
}

variable "project_name" {
  description = "The name of the project"
  type        = string
}

variable "environment" {
  description = "The deployment environment"
  type        = string
}