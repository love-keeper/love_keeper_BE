variable "project_name" {
  description = "프로젝트 이름"
  type        = string
  default     = "lovekeeper"
}

variable "environment" {
  description = "환경 (dev, release 등)"
  type        = string
}

variable "vpc_id" {
  description = "보안 그룹을 생성할 VPC ID"
  type        = string
}

variable "create_bastion" {
  description = "배스천 호스트 보안 그룹 생성 여부"
  type        = bool
  default     = true
}

variable "admin_cidrs" {
  description = "관리자 액세스를 허용할 IP CIDR 블록 리스트"
  type        = list(string)
  default     = ["0.0.0.0/0"] # 실제 구현 시에는 특정 IP로 제한해야 함
}

variable "bastion_sg_id" {
  description = "기존 배스천 호스트 보안 그룹 ID (있는 경우)"
  type        = string
  default     = ""
}

variable "s3_bucket_name" {
  description = "접근할 S3 버킷 이름"
  type        = string
  default     = "lovekeeper-dev"
}
