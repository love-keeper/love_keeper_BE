variable "vpc_id" {
  description = "The ID of the VPC"
  type        = string
}

variable "subnet_ids" {
  description = "The IDs of the subnets"
  type        = list(string)
}

variable "redis_sg_id" {
  description = "The ID of the Redis security group"
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

variable "redis_node_type" {
  description = "The node type of the Redis cluster"
  type        = string
}

variable "redis_port" {
  description = "The port of the Redis cluster"
  type        = number
  default     = 6379
}