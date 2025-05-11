output "redis_id" {
  description = "The ID of the Redis cluster"
  value       = aws_elasticache_replication_group.main.id
}

output "redis_endpoint" {
  description = "The endpoint of the Redis cluster"
  value       = aws_elasticache_replication_group.main.primary_endpoint_address
}

output "redis_port" {
  description = "The port of the Redis cluster"
  value       = var.redis_port
}