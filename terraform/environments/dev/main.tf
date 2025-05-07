provider "aws" {
  region = "ap-northeast-2"
}

module "vpc" {
  source = "../../modules/vpc"

  environment           = "dev"
  vpc_cidr              = "10.0.0.0/16"
  public_subnet_1_cidr  = "10.0.1.0/24"
  public_subnet_2_cidr  = "10.0.2.0/24"
  private_subnet_1_cidr = "10.0.3.0/24"
  private_subnet_2_cidr = "10.0.4.0/24"
}

module "security" {
  source = "../../modules/security"

  environment = "dev"
  vpc_id      = module.vpc.vpc_id
  bastion_allowed_ips = ["0.0.0.0/0"] # 실제 운영에서는 특정 IP만 허용하도록 변경 필요
}

module "bastion" {
  source = "../../modules/bastion"

  environment             = "dev"
  bastion_security_group_id = module.security.bastion_security_group_id
  public_subnet_id        = module.vpc.public_subnet_1_id
  public_key_path         = "/Users/parkdongkyu/my_project/love-keeper-dev.pem.pub" # 실제 키 파일 경로로 수정 필요
}

module "alb" {
  source = "../../modules/alb"

  environment          = "dev"
  vpc_id               = module.vpc.vpc_id
  public_subnet_1_id   = module.vpc.public_subnet_1_id
  public_subnet_2_id   = module.vpc.public_subnet_2_id
  alb_security_group_id = module.security.alb_security_group_id
}

module "rds" {
  source = "../../modules/rds"

  environment           = "dev"
  private_subnet_1_id   = module.vpc.private_subnet_1_id
  private_subnet_2_id   = module.vpc.private_subnet_2_id
  rds_security_group_id = module.security.rds_security_group_id
  allocated_storage     = 20
  instance_class        = "db.t3.micro"
  db_username           = var.db_username
  db_password           = var.db_password
  multi_az              = false
}

module "elasticache" {
  source = "../../modules/elasticache"

  environment            = "dev"
  private_subnet_1_id    = module.vpc.private_subnet_1_id
  private_subnet_2_id    = module.vpc.private_subnet_2_id
  redis_security_group_id = module.security.redis_security_group_id
  node_type              = "cache.t3.micro"
  automatic_failover_enabled = false
  num_cache_clusters     = 1
}

module "ecs" {
  source = "../../modules/ecs"

  environment           = "dev"
  private_subnet_1_id   = module.vpc.private_subnet_1_id
  private_subnet_2_id   = module.vpc.private_subnet_2_id
  ecs_security_group_id = module.security.ecs_security_group_id
  target_group_arn      = module.alb.target_group_arn
  task_cpu              = "256"
  task_memory           = "512"
  service_desired_count = 1
  s3_bucket_name        = var.s3_bucket_name
  mail_username         = var.mail_username
  mail_password         = var.mail_password
  jwt_secret            = var.jwt_secret
  aws_access_key        = var.aws_access_key
  aws_secret_key        = var.aws_secret_key
}

module "route53" {
  source = "../../modules/route53"

  environment   = "dev"
  domain_name   = "lovekeeper.site"
  alb_dns_name  = module.alb.alb_dns_name
  alb_zone_id   = module.alb.alb_zone_id
}

module "acm" {
  source = "../../modules/acm"

  environment       = "dev"
  domain_name       = "lovekeeper.site"
  route53_zone_id   = module.route53.zone_id
  load_balancer_arn = module.alb.alb_arn
  target_group_arn  = module.alb.target_group_arn
}
