/*
* LoveKeeper 릴리스(release) 환경 인프라
* 모든 리소스를 조합하여 릴리스 환경을 구성합니다.
*/

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # 상태 파일을 S3에 저장하려면 아래 주석을 제거하세요
  /*
  backend "s3" {
    bucket         = "lovekeeper-terraform-state"
    key            = "release/terraform.tfstate"
    region         = "ap-northeast-2"
    encrypt        = true
    dynamodb_table = "lovekeeper-terraform-locks"
  }
  */
}

provider "aws" {
  region = var.region
}

locals {
  environment = "release"
}

# 네트워킹 모듈
module "networking" {
  source = "../../modules/networking"

  project_name = var.project_name
  environment  = local.environment
  vpc_cidr     = var.vpc_cidr

  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
  availability_zones   = var.availability_zones
}

# 보안 모듈
module "security" {
  source = "../../modules/security"

  project_name    = var.project_name
  environment     = local.environment
  vpc_id          = module.networking.vpc_id
  create_bastion  = var.create_bastion
  admin_cidrs     = var.admin_cidrs
  s3_bucket_name  = "${var.project_name}-${local.environment}"
}

# 스토리지 모듈
module "storage" {
  source = "../../modules/storage"

  project_name    = var.project_name
  environment     = local.environment
  allowed_origins = var.allowed_origins
  enable_versioning = true
}

# DB 비밀번호를 AWS Secrets Manager에 저장
resource "aws_secretsmanager_secret" "db_password" {
  name = "${var.project_name}/${local.environment}/db-password"
  
  tags = {
    Name        = "${var.project_name}-${local.environment}-db-password"
    Environment = local.environment
  }
}

resource "aws_secretsmanager_secret_version" "db_password" {
  secret_id     = aws_secretsmanager_secret.db_password.id
  secret_string = var.db_password
}

# 데이터베이스 모듈
module "database" {
  source = "../../modules/database"

  project_name         = var.project_name
  environment          = local.environment
  subnet_ids           = module.networking.private_subnet_ids
  security_group_id    = module.security.db_security_group_id
  redis_security_group_id = module.security.redis_security_group_id
  
  db_name              = var.db_name
  db_username          = var.db_username
  db_password          = var.db_password
  db_instance_class    = var.db_instance_class
  db_allocated_storage = var.db_allocated_storage
  redis_node_type      = var.redis_node_type
  db_backup_retention_period = 14
}

# 컴퓨트 모듈
module "compute" {
  source = "../../modules/compute"

  project_name          = var.project_name
  environment           = local.environment
  region                = var.region
  vpc_id                = module.networking.vpc_id
  public_subnet_ids     = module.networking.public_subnet_ids
  private_subnet_ids    = module.networking.private_subnet_ids
  ecs_security_group_id = module.security.ecs_security_group_id
  alb_security_group_id = module.security.alb_security_group_id
  
  execution_role_arn    = module.security.ecs_execution_role_arn
  task_role_arn         = module.security.ecs_task_role_arn
  
  create_ecr            = false
  use_ecr               = true
  ecr_repository_url    = var.ecr_repository_url
  image_tag             = var.image_tag
  
  db_endpoint           = module.database.db_instance_endpoint
  db_name               = module.database.db_instance_name
  db_username           = var.db_username
  db_password_arn       = aws_secretsmanager_secret.db_password.arn
  
  redis_endpoint        = module.database.redis_endpoint
  redis_port            = module.database.redis_port
  
  task_cpu              = var.task_cpu
  task_memory           = var.task_memory
  desired_count         = var.desired_count
  health_check_path     = var.health_check_path
  
  create_alb            = var.create_alb
  ssl_certificate_arn   = var.ssl_certificate_arn
  
  create_bastion        = var.create_bastion
  bastion_key_name      = var.bastion_key_name
  bastion_security_group_id = module.security.bastion_security_group_id
}

# Route 53 레코드 (선택 사항)
resource "aws_route53_record" "app" {
  count = var.create_route53_record ? 1 : 0
  
  zone_id = var.route53_zone_id
  name    = var.domain_name
  type    = "A"

  alias {
    name                   = module.compute.alb_dns_name
    zone_id                = var.alb_zone_id
    evaluate_target_health = true
  }
}
