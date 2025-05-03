output "db_instance_endpoint" {
  description = "RDS 인스턴스 엔드포인트"
  value       = aws_db_instance.main.endpoint
}

output "db_instance_address" {
  description = "RDS 인스턴스 주소"
  value       = aws_db_instance.main.address
}

output "db_instance_port" {
  description = "RDS 인스턴스 포트"
  value       = aws_db_instance.main.port
}

output "db_instance_name" {
  description = "RDS 데이터베이스 이름"
  value       = aws_db_instance.main.db_name
}

output "db_instance_id" {
  description = "RDS 인스턴스 ID"
  value       = aws_db_instance.main.id
}

output "redis_endpoint" {
  description = "Redis 클러스터 엔드포인트"
  value       = aws_elasticache_cluster.main.cache_nodes[0].address
}

output "redis_port" {
  description = "Redis 클러스터 포트"
  value       = aws_elasticache_cluster.main.port
}

output "redis_cluster_id" {
  description = "Redis 클러스터 ID"
  value       = aws_elasticache_cluster.main.id
}
