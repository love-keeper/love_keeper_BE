variable "vpc_id" {
  description = "The ID of the VPC"
  type        = string
}

variable "subnet_ids" {
  description = "The IDs of the subnets"
  type        = list(string)
}

variable "db_sg_id" {
  description = "The ID of the DB security group"
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

variable "db_name" {
  description = "The name of the database"
  type        = string
}

variable "db_instance_class" {
  description = "The instance class of the database"
  type        = string
}

variable "db_allocated_storage" {
  description = "The allocated storage for the database (in GB)"
  type        = number
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