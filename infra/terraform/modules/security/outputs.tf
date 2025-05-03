output "alb_security_group_id" {
  description = "Application Load Balancer 보안 그룹 ID"
  value       = aws_security_group.alb.id
}

output "ecs_security_group_id" {
  description = "ECS 보안 그룹 ID"
  value       = aws_security_group.ecs.id
}

output "db_security_group_id" {
  description = "데이터베이스 보안 그룹 ID"
  value       = aws_security_group.db.id
}

output "redis_security_group_id" {
  description = "Redis 보안 그룹 ID"
  value       = aws_security_group.redis.id
}

output "bastion_security_group_id" {
  description = "배스천 호스트 보안 그룹 ID"
  value       = var.create_bastion ? aws_security_group.bastion[0].id : ""
}

output "ecs_execution_role_arn" {
  description = "ECS 태스크 실행 역할 ARN"
  value       = aws_iam_role.ecs_execution.arn
}

output "ecs_task_role_arn" {
  description = "ECS 태스크 역할 ARN"
  value       = aws_iam_role.ecs_task.arn
}

output "s3_access_policy_arn" {
  description = "S3 접근 정책 ARN"
  value       = aws_iam_policy.s3_access.arn
}

output "firebase_access_policy_arn" {
  description = "Firebase 접근 정책 ARN"
  value       = aws_iam_policy.firebase_access.arn
}
