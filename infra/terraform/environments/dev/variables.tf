variable "project_name" {
  description = "프로젝트 이름"
  type        = string
  default     = "lovekeeper"
}

variable "region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"
}

# 네트워킹 변수
variable "vpc_cidr" {
  description = "VPC CIDR 블록"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "퍼블릭 서브넷 CIDR 블록 리스트"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "프라이빗 서브넷 CIDR 블록 리스트"
  type        = list(string)
  default     = ["10.0.3.0/24", "10.0.4.0/24"]
}

variable "availability_zones" {
  description = "사용할 가용 영역 리스트"
  type        = list(string)
  default     = ["ap-northeast-2a", "ap-northeast-2c"]
}

# 보안 변수
variable "create_bastion" {
  description = "배스천 호스트 생성 여부"
  type        = bool
  default     = true
}

variable "admin_cidrs" {
  description = "관리자 액세스를 허용할 IP CIDR 블록 리스트"
  type        = list(string)
  default     = ["0.0.0.0/0"] # 실제 구현 시에는 특정 IP로 제한해야 함
}

# 데이터베이스 변수
variable "db_name" {
  description = "RDS 데이터베이스 이름"
  type        = string
  default     = "lovekeeper_dev"
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
  default     = "changeme" # 실제 구현 시에는 안전한 비밀번호 사용 필요
}

variable "jwt_secret" {
  description = "JWT 서명용 비밀키"
  type        = string
  sensitive   = true
  default     = "skdjflksdjflskdfjdfiwjeofijoiasjdfiajsvckmvxkcvmzkxcmvlmklkdgjasdjfoiwjoedifjwodifjasidfj" # 실제 구현 시에는 랜덤 생성한 길고 복잡한 값 사용 필요
}

variable "db_instance_class" {
  description = "RDS 인스턴스 타입"
  type        = string
  default     = "db.t3.small"
}

variable "db_allocated_storage" {
  description = "RDS 인스턴스에 할당할 스토리지 용량 (GB)"
  type        = number
  default     = 20
}

variable "redis_node_type" {
  description = "Redis 노드 타입"
  type        = string
  default     = "cache.t3.small"
}

# 컴퓨트 변수
variable "create_ecr" {
  description = "ECR 리포지토리 생성 여부"
  type        = bool
  default     = true
}

variable "image_tag" {
  description = "컨테이너 이미지 태그"
  type        = string
  default     = "dev"
}

variable "task_cpu" {
  description = "ECS 태스크 CPU 유닛"
  type        = string
  default     = "512"
}

variable "task_memory" {
  description = "ECS 태스크 메모리 (MB)"
  type        = string
  default     = "1024"
}

variable "desired_count" {
  description = "ECS 서비스 원하는 작업 수"
  type        = number
  default     = 1
}

variable "health_check_path" {
  description = "ALB 헬스 체크 경로"
  type        = string
  default     = "/actuator/health"
}

variable "create_alb" {
  description = "ALB 생성 여부"
  type        = bool
  default     = true
}

variable "ssl_certificate_arn" {
  description = "SSL 인증서 ARN"
  type        = string
  default     = "" # ACM 인증서 ARN 입력 필요
}

variable "bastion_key_name" {
  description = "배스천 호스트 키 페어 이름"
  type        = string
  default     = "love-keeper-bastion" # 배스천 호스트용 키페어
}

# S3 변수
variable "allowed_origins" {
  description = "CORS 허용 오리진 목록"
  type        = list(string)
  default     = ["*"] # 실제 구현 시에는 특정 도메인으로 제한해야 함
}
