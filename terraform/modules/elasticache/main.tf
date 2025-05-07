resource "aws_elasticache_subnet_group" "main" {
  name       = "${var.environment}-redis-subnet-group"
  subnet_ids = [var.private_subnet_1_id, var.private_subnet_2_id]

  tags = {
    Name        = "${var.environment}-redis-subnet-group"
    Environment = var.environment
  }
}

resource "aws_elasticache_parameter_group" "main" {
  name   = "${var.environment}-redis-parameter-group"
  family = "redis7"

  tags = {
    Name        = "${var.environment}-redis-parameter-group"
    Environment = var.environment
  }
}

resource "aws_elasticache_replication_group" "main" {
  replication_group_id       = "${var.environment}-redis"
  description                = "Redis cluster for ${var.environment} environment"
  node_type                  = var.node_type
  port                       = 6379
  parameter_group_name       = aws_elasticache_parameter_group.main.name
  subnet_group_name          = aws_elasticache_subnet_group.main.name
  security_group_ids         = [var.redis_security_group_id]
  automatic_failover_enabled = var.automatic_failover_enabled
  num_cache_clusters         = var.num_cache_clusters
  at_rest_encryption_enabled = true
  transit_encryption_enabled = true

  tags = {
    Name        = "${var.environment}-redis"
    Environment = var.environment
  }
}

# Redis 호스트를 AWS SSM 파라미터 저장소에 저장
resource "aws_ssm_parameter" "redis_host" {
  name        = "/${var.environment}/redis/host"
  description = "Redis hostname for ${var.environment} environment"
  type        = "String"
  value       = aws_elasticache_replication_group.main.primary_endpoint_address

  tags = {
    Environment = var.environment
  }
}
