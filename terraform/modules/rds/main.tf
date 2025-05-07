resource "aws_db_subnet_group" "main" {
  name       = "${var.environment}-db-subnet-group"
  subnet_ids = [var.private_subnet_1_id, var.private_subnet_2_id]

  tags = {
    Name        = "${var.environment}-db-subnet-group"
    Environment = var.environment
  }
}

resource "aws_db_parameter_group" "main" {
  name   = "${var.environment}-db-parameter-group"
  family = "mysql8.0"

  parameter {
    name  = "character_set_server"
    value = "utf8mb4"
  }

  parameter {
    name  = "character_set_client"
    value = "utf8mb4"
  }

  tags = {
    Name        = "${var.environment}-db-parameter-group"
    Environment = var.environment
  }
}

resource "aws_db_instance" "main" {
  identifier              = "${var.environment}-db"
  allocated_storage       = var.allocated_storage
  storage_type            = "gp3"
  engine                  = "mysql"
  engine_version          = "8.0"
  instance_class          = var.instance_class
  db_name                 = "love_keeper"
  username                = var.db_username
  password                = var.db_password
  parameter_group_name    = aws_db_parameter_group.main.name
  db_subnet_group_name    = aws_db_subnet_group.main.name
  vpc_security_group_ids  = [var.rds_security_group_id]
  multi_az                = var.multi_az
  backup_retention_period = 7
  backup_window           = "03:00-04:00"
  maintenance_window      = "Mon:04:00-Mon:05:00"
  skip_final_snapshot     = true
  publicly_accessible     = false
  apply_immediately       = true

  tags = {
    Name        = "${var.environment}-db"
    Environment = var.environment
  }
}

# 비밀번호를 AWS SSM 파라미터 저장소에 저장
resource "aws_ssm_parameter" "db_password" {
  name        = "/${var.environment}/db/password"
  description = "Database password for ${var.environment} environment"
  type        = "SecureString"
  value       = var.db_password

  tags = {
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "db_username" {
  name        = "/${var.environment}/db/username"
  description = "Database username for ${var.environment} environment"
  type        = "SecureString"
  value       = var.db_username

  tags = {
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "db_host" {
  name        = "/${var.environment}/db/host"
  description = "Database hostname for ${var.environment} environment"
  type        = "String"
  value       = aws_db_instance.main.address

  tags = {
    Environment = var.environment
  }
}
