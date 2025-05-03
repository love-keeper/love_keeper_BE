variable "project_name" {
  description = "프로젝트 이름"
  type        = string
  default     = "lovekeeper"
}

variable "environment" {
  description = "환경 (dev, release 등)"
  type        = string
}

variable "enable_versioning" {
  description = "S3 버킷 버전 관리 활성화 여부"
  type        = bool
  default     = false
}

variable "expiration_days" {
  description = "객체 만료 일수"
  type        = number
  default     = 365
}

variable "noncurrent_version_expiration_days" {
  description = "이전 버전 객체 만료 일수"
  type        = number
  default     = 30
}

variable "allowed_origins" {
  description = "CORS 허용 오리진 목록"
  type        = list(string)
  default     = ["*"]
}

variable "bucket_policy" {
  description = "S3 버킷 정책 JSON (선택 사항)"
  type        = string
  default     = ""
}
