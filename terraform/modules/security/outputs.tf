output "alb_sg_id" {
  description = "The ID of the ALB security group"
  value       = aws_security_group.alb.id
}

output "ecs_sg_id" {
  description = "The ID of the ECS security group"
  value       = aws_security_group.ecs.id
}

output "rds_sg_id" {
  description = "The ID of the RDS security group"
  value       = aws_security_group.rds.id
}

output "redis_sg_id" {
  description = "The ID of the Redis security group"
  value       = aws_security_group.redis.id
}

output "bastion_sg_id" {
  description = "The ID of the Bastion Host security group"
  value       = aws_security_group.bastion.id
}