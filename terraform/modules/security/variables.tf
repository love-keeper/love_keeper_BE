variable "environment" {
  description = "Environment (dev or release)"
  type        = string
}

variable "vpc_id" {
  description = "ID of the VPC"
  type        = string
}

variable "bastion_allowed_ips" {
  description = "List of IPs allowed to connect to the bastion host"
  type        = list(string)
  default     = ["0.0.0.0/0"]  # 실제 운영에서는 특정 IP만 허용하도록 변경 필요
}
