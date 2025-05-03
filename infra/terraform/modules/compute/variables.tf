variable "project_name" {
  description = "프로젝트 이름"
  type        = string
  default     = "lovekeeper"
}

variable "environment" {
  description = "환경 (dev, release 등)"
  type        = string
}

variable "region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"
}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "public_subnet_ids" {
  description = "퍼블릭 서브넷 ID 리스트"
  type        = list(string)
}

variable "private_subnet_ids" {
  description = "프라이빗 서브넷 ID 리스트"
  type        = list(string)
}

variable "ecs_security_group_id" {
  description = "ECS 보안 그룹 ID"
  type        = string
}

variable "alb_security_group_id" {
  description = "ALB 보안 그룹 ID"
  type        = string
}

variable "bastion_security_group_id" {
  description = "배스천 호스트 보안 그룹 ID"
  type        = string
  default     = ""
}

variable "execution_role_arn" {
  description = "ECS 태스크 실행 역할 ARN"
  type        = string
}

variable "task_role_arn" {
  description = "ECS 태스크 역할 ARN"
  type        = string
}

variable "container_image" {
  description = "컨테이너 이미지 (ECR 외부 이미지 사용 시)"
  type        = string
  default     = ""
}

variable "create_ecr" {
  description = "ECR 리포지토리 생성 여부"
  type        = bool
  default     = true
}

variable "use_ecr" {
  description = "ECR 리포지토리 사용 여부"
  type        = bool
  default     = true
}

variable "ecr_repository_url" {
  description = "기존 ECR 리포지토리 URL"
  type        = string
  default     = ""
}

variable "image_tag" {
  description = "컨테이너 이미지 태그"
  type        = string
  default     = "latest"
}

variable "container_port" {
  description = "컨테이너 포트"
  type        = number
  default     = 8082
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

variable "db_endpoint" {
  description = "데이터베이스 엔드포인트"
  type        = string
}

variable "db_name" {
  description = "데이터베이스 이름"
  type        = string
}

variable "db_username" {
  description = "데이터베이스 사용자 이름"
  type        = string
}

variable "db_password_arn" {
  description = "데이터베이스 비밀번호의 AWS Secrets Manager ARN"
  type        = string
}

variable "redis_endpoint" {
  description = "Redis 엔드포인트"
  type        = string
}

variable "redis_port" {
  description = "Redis 포트"
  type        = number
  default     = 6379
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

variable "alb_listener_arn" {
  description = "기존 ALB 리스너 ARN (공유 ALB 사용 시)"
  type        = string
  default     = ""
}

variable "alb_listener_rule_priority" {
  description = "ALB 리스너 규칙 우선순위 (공유 ALB 사용 시)"
  type        = number
  default     = 100
}

variable "alb_host_header" {
  description = "ALB 호스트 헤더 (공유 ALB 사용 시)"
  type        = string
  default     = ""
}

variable "ssl_certificate_arn" {
  description = "SSL 인증서 ARN"
  type        = string
  default     = ""
}

variable "create_bastion" {
  description = "배스천 호스트 생성 여부"
  type        = bool
  default     = false
}

variable "bastion_ami_id" {
  description = "배스천 호스트 AMI ID"
  type        = string
  default     = "ami-0fd0765afb77bcca7" # Amazon Linux 2 AMI
}

variable "bastion_instance_type" {
  description = "배스천 호스트 인스턴스 타입"
  type        = string
  default     = "t3.micro"
}

variable "bastion_key_name" {
  description = "배스천 호스트 키 페어 이름"
  type        = string
  default     = ""
}

variable "additional_environment_variables" {
  description = "추가 환경 변수 리스트"
  type        = list(object({
    name  = string
    value = string
  }))
  default     = []
}
