variable "project_name" {
  description = "프로젝트 이름"
  type        = string
  default     = "lovekeeper"
}

variable "environment" {
  description = "환경 (dev, release 등)"
  type        = string
}

variable "subnet_ids" {
  description = "데이터베이스 서브넷 그룹에 사용할 서브넷 ID 리스트"
  type        = list(string)
}

variable "security_group_id" {
  description = "RDS 인스턴스에 적용할 보안 그룹 ID"
  type        = string
}

variable "redis_security_group_id" {
  description = "Redis 클러스터에 적용할 보안 그룹 ID"
  type        = string
}

variable "db_allocated_storage" {
  description = "RDS 인스턴스에 할당할 스토리지 용량 (GB)"
  type        = number
  default     = 20
}

variable "db_instance_class" {
  description = "RDS 인스턴스 타입"
  type        = string
  default     = "db.t3.small"
}

variable "db_name" {
  description = "RDS 데이터베이스 이름"
  type        = string
}

variable "db_username" {
  description = "RDS 마스터 사용자 이름"
  type        = string
  default     = "admin"
}

variable "db_password" {
  description = "RDS 마스터 사용자 비밀번호"
  type        = string
  sensitive   = true
}

variable "db_backup_retention_period" {
  description = "RDS 백업 보존 기간 (일)"
  type        = number
  default     = 7
}

variable "redis_node_type" {
  description = "Redis 노드 타입"
  type        = string
  default     = "cache.t3.small"
}
