output "redis_endpoint" {
  description = "Redis primary endpoint"
  value       = aws_elasticache_replication_group.main.primary_endpoint_address
}

output "redis_replication_group_id" {
  description = "ID of Redis replication group"
  value       = aws_elasticache_replication_group.main.id
}
