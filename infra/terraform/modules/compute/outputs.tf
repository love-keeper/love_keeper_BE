output "ecr_repository_url" {
  description = "ECR 리포지토리 URL"
  value       = var.create_ecr ? (length(aws_ecr_repository.app) > 0 ? aws_ecr_repository.app[0].repository_url : "") : var.ecr_repository_url
}

output "ecs_cluster_id" {
  description = "ECS 클러스터 ID"
  value       = aws_ecs_cluster.main.id
}

output "ecs_cluster_name" {
  description = "ECS 클러스터 이름"
  value       = aws_ecs_cluster.main.name
}

output "ecs_service_id" {
  description = "ECS 서비스 ID"
  value       = aws_ecs_service.app.id
}

output "ecs_service_name" {
  description = "ECS 서비스 이름"
  value       = aws_ecs_service.app.name
}

output "ecs_task_definition_arn" {
  description = "ECS 태스크 정의 ARN"
  value       = aws_ecs_task_definition.app.arn
}

output "alb_dns_name" {
  description = "ALB DNS 이름"
  value       = var.create_alb ? (length(aws_lb.app) > 0 ? aws_lb.app[0].dns_name : "") : ""
}

output "alb_arn" {
  description = "ALB ARN"
  value       = var.create_alb ? (length(aws_lb.app) > 0 ? aws_lb.app[0].arn : "") : ""
}

output "alb_target_group_arn" {
  description = "ALB 대상 그룹 ARN"
  value       = aws_lb_target_group.app.arn
}

output "bastion_public_ip" {
  description = "배스천 호스트 퍼블릭 IP"
  value       = var.create_bastion ? (length(aws_instance.bastion) > 0 ? aws_instance.bastion[0].public_ip : "") : ""
}

output "bastion_instance_id" {
  description = "배스천 호스트 인스턴스 ID"
  value       = var.create_bastion ? (length(aws_instance.bastion) > 0 ? aws_instance.bastion[0].id : "") : ""
}

output "cloudwatch_log_group_name" {
  description = "CloudWatch 로그 그룹 이름"
  value       = aws_cloudwatch_log_group.ecs.name
}
