resource "aws_elasticache_subnet_group" "main" {
  name       = "${var.project_name}-${var.environment}-redis-subnet-group"
  subnet_ids = var.subnet_ids

  tags = {
    Name        = "${var.project_name}-${var.environment}-redis-subnet-group"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_elasticache_parameter_group" "main" {
  name   = "${var.project_name}-${var.environment}-redis-pg"
  family = "redis6.x"

  parameter {
    name  = "maxmemory-policy"
    value = "allkeys-lru"
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-redis-pg"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_elasticache_replication_group" "main" {
  replication_group_id          = "${var.project_name}-${var.environment}-redis"
  description                   = "${var.project_name} ${var.environment} Redis cluster"
  node_type                     = var.redis_node_type
  port                          = var.redis_port
  parameter_group_name          = aws_elasticache_parameter_group.main.name
  subnet_group_name             = aws_elasticache_subnet_group.main.name
  security_group_ids            = [var.redis_sg_id]
  automatic_failover_enabled    = var.environment == "prod" ? true : false
  multi_az_enabled              = var.environment == "prod" ? true : false
  num_cache_clusters            = var.environment == "prod" ? 2 : 1
  at_rest_encryption_enabled    = true
  transit_encryption_enabled    = true
  
  tags = {
    Name        = "${var.project_name}-${var.environment}-redis"
    Project     = var.project_name
    Environment = var.environment
  }
}