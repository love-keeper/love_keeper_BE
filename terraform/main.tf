provider "aws" {
  region = var.aws_region
}

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket = "lovekeeper-terraform-state"
    key    = "terraform.tfstate"
    region = "ap-northeast-2"
  }
}

# Import modules
module "vpc" {
  source = "./modules/vpc"
  
  vpc_cidr             = var.vpc_cidr
  public_subnet_1_cidr = var.public_subnet_1_cidr
  public_subnet_2_cidr = var.public_subnet_2_cidr
  private_subnet_1_cidr = var.private_subnet_1_cidr
  private_subnet_2_cidr = var.private_subnet_2_cidr
  availability_zones   = var.availability_zones
  project_name         = var.project_name
  environment          = var.environment
}

module "security" {
  source = "./modules/security"
  
  vpc_id      = module.vpc.vpc_id
  project_name = var.project_name
  environment = var.environment
}

module "bastion" {
  source = "./modules/bastion"
  
  vpc_id            = module.vpc.vpc_id
  public_subnet_id  = module.vpc.public_subnet_1_id
  bastion_sg_id     = module.security.bastion_sg_id
  key_name          = var.key_name
  project_name      = var.project_name
  environment       = var.environment
}

module "alb" {
  source = "./modules/alb"
  
  vpc_id            = module.vpc.vpc_id
  public_subnet_ids = [module.vpc.public_subnet_1_id, module.vpc.public_subnet_2_id]
  alb_sg_id         = module.security.alb_sg_id
  project_name      = var.project_name
  environment       = var.environment
}

module "ecs_dev" {
  source = "./modules/ecs"
  
  vpc_id              = module.vpc.vpc_id
  private_subnet_ids  = [module.vpc.private_subnet_1_id]
  ecs_sg_id           = module.security.ecs_sg_id
  alb_target_group_arn = module.alb.target_group_dev_arn
  ecr_repository_url  = module.ecr.repository_url
  project_name        = var.project_name
  environment         = "dev"
  container_port      = var.container_port
  cpu                 = var.ecs_task_cpu_dev
  memory              = var.ecs_task_memory_dev
  desired_count       = var.ecs_task_count_dev
  aws_region          = var.aws_region
  
  environment_variables = [
    { name = "SPRING_PROFILES_ACTIVE", value = "dev" },
    { name = "DEV_DB_HOST", value = module.rds_dev.db_endpoint },
    { name = "DEV_REDIS_HOST", value = module.redis_dev.redis_endpoint }
  ]
  
  secrets = [
    { name = "DEV_DB_USERNAME", valueFrom = "${module.secrets.dev_db_username_arn}" },
    { name = "DEV_DB_PASSWORD", valueFrom = "${module.secrets.dev_db_password_arn}" },
    { name = "JWT_SECRET", valueFrom = "${module.secrets.jwt_secret_arn}" },
    { name = "AWS_ACCESS_KEY", valueFrom = "${module.secrets.aws_access_key_arn}" },
    { name = "AWS_SECRET_KEY", valueFrom = "${module.secrets.aws_secret_key_arn}" },
    { name = "AWS_S3_BUCKET", valueFrom = "${module.secrets.aws_s3_bucket_arn}" },
    { name = "MAIL_USERNAME", valueFrom = "${module.secrets.mail_username_arn}" },
    { name = "MAIL_PASSWORD", valueFrom = "${module.secrets.mail_password_arn}" }
  ]
}

module "ecs_prod" {
  source = "./modules/ecs"
  
  vpc_id              = module.vpc.vpc_id
  private_subnet_ids  = [module.vpc.private_subnet_2_id]
  ecs_sg_id           = module.security.ecs_sg_id
  alb_target_group_arn = module.alb.target_group_prod_arn
  ecr_repository_url  = module.ecr.repository_url
  project_name        = var.project_name
  environment         = "prod"
  container_port      = var.container_port
  cpu                 = var.ecs_task_cpu_prod
  memory              = var.ecs_task_memory_prod
  desired_count       = var.ecs_task_count_prod
  aws_region          = var.aws_region
  
  environment_variables = [
    { name = "SPRING_PROFILES_ACTIVE", value = "prod" },
    { name = "PROD_DB_HOST", value = module.rds_prod.db_endpoint },
    { name = "PROD_REDIS_HOST", value = module.redis_prod.redis_endpoint }
  ]
  
  secrets = [
    { name = "PROD_DB_USERNAME", valueFrom = "${module.secrets.prod_db_username_arn}" },
    { name = "PROD_DB_PASSWORD", valueFrom = "${module.secrets.prod_db_password_arn}" },
    { name = "JWT_SECRET", valueFrom = "${module.secrets.jwt_secret_arn}" },
    { name = "AWS_ACCESS_KEY", valueFrom = "${module.secrets.aws_access_key_arn}" },
    { name = "AWS_SECRET_KEY", valueFrom = "${module.secrets.aws_secret_key_arn}" },
    { name = "AWS_S3_BUCKET", valueFrom = "${module.secrets.aws_s3_bucket_arn}" },
    { name = "MAIL_USERNAME", valueFrom = "${module.secrets.mail_username_arn}" },
    { name = "MAIL_PASSWORD", valueFrom = "${module.secrets.mail_password_arn}" }
  ]
}

module "rds_dev" {
  source = "./modules/rds"
  
  vpc_id               = module.vpc.vpc_id
  subnet_ids           = [module.vpc.private_subnet_1_id]
  db_sg_id             = module.security.rds_sg_id
  project_name         = var.project_name
  environment          = "dev"
  db_name              = "love_keeper"
  db_instance_class    = var.db_instance_class_dev
  db_allocated_storage = var.db_allocated_storage_dev
  db_username          = var.db_username_dev
  db_password          = var.db_password_dev
}

module "rds_prod" {
  source = "./modules/rds"
  
  vpc_id               = module.vpc.vpc_id
  subnet_ids           = [module.vpc.private_subnet_2_id]
  db_sg_id             = module.security.rds_sg_id
  project_name         = var.project_name
  environment          = "prod"
  db_name              = "love_keeper"
  db_instance_class    = var.db_instance_class_prod
  db_allocated_storage = var.db_allocated_storage_prod
  db_username          = var.db_username_prod
  db_password          = var.db_password_prod
}

module "redis_dev" {
  source = "./modules/redis"
  
  vpc_id            = module.vpc.vpc_id
  subnet_ids        = [module.vpc.private_subnet_1_id]
  redis_sg_id       = module.security.redis_sg_id
  project_name      = var.project_name
  environment       = "dev"
  redis_node_type   = var.redis_node_type_dev
  redis_port        = var.redis_port
}

module "redis_prod" {
  source = "./modules/redis"
  
  vpc_id            = module.vpc.vpc_id
  subnet_ids        = [module.vpc.private_subnet_2_id]
  redis_sg_id       = module.security.redis_sg_id
  project_name      = var.project_name
  environment       = "prod"
  redis_node_type   = var.redis_node_type_prod
  redis_port        = var.redis_port
}

module "s3" {
  source = "./modules/s3"
  
  project_name = var.project_name
  environment  = var.environment
}

module "ecr" {
  source = "./modules/ecr"
  
  project_name = var.project_name
  environment  = var.environment
}

module "secrets" {
  source = "./modules/secrets"
  
  project_name = var.project_name
  environment  = var.environment
  
  # Dev secrets
  dev_db_username = var.db_username_dev
  dev_db_password = var.db_password_dev
  
  # Prod secrets
  prod_db_username = var.db_username_prod
  prod_db_password = var.db_password_prod
  
  # Common secrets
  jwt_secret       = var.jwt_secret
  aws_access_key   = var.aws_access_key
  aws_secret_key   = var.aws_secret_key
  aws_s3_bucket    = var.aws_s3_bucket
  mail_username    = var.mail_username
  mail_password    = var.mail_password
}