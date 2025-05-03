output "vpc_id" {
  description = "VPC ID"
  value       = module.networking.vpc_id
}

output "public_subnet_ids" {
  description = "퍼블릭 서브넷 ID 리스트"
  value       = module.networking.public_subnet_ids
}

output "private_subnet_ids" {
  description = "프라이빗 서브넷 ID 리스트"
  value       = module.networking.private_subnet_ids
}

output "db_instance_endpoint" {
  description = "RDS 인스턴스 엔드포인트"
  value       = module.database.db_instance_endpoint
}

output "redis_endpoint" {
  description = "Redis 클러스터 엔드포인트"
  value       = module.database.redis_endpoint
}

output "ecs_cluster_name" {
  description = "ECS 클러스터 이름"
  value       = module.compute.ecs_cluster_name
}

output "ecs_service_name" {
  description = "ECS 서비스 이름"
  value       = module.compute.ecs_service_name
}

output "alb_dns_name" {
  description = "ALB DNS 이름"
  value       = module.compute.alb_dns_name
}

output "bastion_public_ip" {
  description = "배스천 호스트 퍼블릭 IP"
  value       = module.compute.bastion_public_ip
}

output "s3_bucket_name" {
  description = "S3 버킷 이름"
  value       = module.storage.bucket_id
}

output "s3_bucket_domain_name" {
  description = "S3 버킷 도메인 이름"
  value       = module.storage.bucket_domain_name
}

output "app_url" {
  description = "애플리케이션 URL"
  value       = var.create_route53_record ? "https://${var.domain_name}" : "https://${module.compute.alb_dns_name}"
}
