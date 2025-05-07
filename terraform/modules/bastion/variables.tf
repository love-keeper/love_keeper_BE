variable "environment" {
  description = "Environment (dev or release)"
  type        = string
}

variable "ami_id" {
  description = "Amazon Machine Image ID"
  type        = string
  default     = "ami-0fd0765afb77bcca7" # Amazon Linux 2023
}

variable "instance_type" {
  description = "Instance type for Bastion host"
  type        = string
  default     = "t3.micro"
}

variable "bastion_security_group_id" {
  description = "Security group ID for Bastion host"
  type        = string
}

variable "public_subnet_id" {
  description = "Public subnet ID where Bastion host will be deployed"
  type        = string
}

variable "public_key_path" {
  description = "Path to public key file"
  type        = string
  default     = "~/.ssh/id_rsa.pub"
}
