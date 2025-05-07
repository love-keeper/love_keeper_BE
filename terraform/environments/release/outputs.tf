output "vpc_id" {
  description = "ID of the VPC"
  value       = module.vpc.vpc_id
}

output "alb_dns_name" {
  description = "DNS name of the ALB"
  value       = module.alb.alb_dns_name
}

output "db_instance_endpoint" {
  description = "Connection endpoint of the RDS instance"
  value       = module.rds.db_instance_endpoint
}

output "redis_endpoint" {
  description = "Redis primary endpoint"
  value       = module.elasticache.redis_endpoint
}

output "bastion_public_ip" {
  description = "Public IP of Bastion instance"
  value       = module.bastion.bastion_public_ip
}

output "ecr_repository_url" {
  description = "URL of the ECR repository"
  value       = module.ecs.ecr_repository_url
}

output "s3_bucket_name" {
  description = "Name of the S3 bucket"
  value       = module.ecs.s3_bucket_name
}

output "certificate_arn" {
  description = "ARN of the ACM certificate"
  value       = module.acm.certificate_arn
}

output "domain_name" {
  description = "Domain name for the application"
  value       = module.route53.domain_name
}

output "https_listener_arn" {
  description = "ARN of the HTTPS listener"
  value       = module.acm.https_listener_arn
}
