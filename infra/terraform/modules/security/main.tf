/*
* LoveKeeper 보안 모듈
* 보안 그룹, IAM 역할 및 정책을 정의합니다.
*/

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# 애플리케이션 로드 밸런서 보안 그룹
resource "aws_security_group" "alb" {
  name        = "${var.project_name}-${var.environment}-alb-sg"
  description = "Security group for ${var.project_name} ALB in ${var.environment}"
  vpc_id      = var.vpc_id

  # HTTP 인바운드 트래픽 허용
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTPS 인바운드 트래픽 허용
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # 모든 아웃바운드 트래픽 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-alb-sg"
    Environment = var.environment
  }
}

# ECS 보안 그룹
resource "aws_security_group" "ecs" {
  name        = "${var.project_name}-${var.environment}-ecs-sg"
  description = "Security group for ${var.project_name} ECS in ${var.environment}"
  vpc_id      = var.vpc_id

  # ALB로부터의 인바운드 트래픽 허용
  ingress {
    from_port       = 8082
    to_port         = 8082
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  # 모든 아웃바운드 트래픽 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-ecs-sg"
    Environment = var.environment
  }
}

# 데이터베이스 보안 그룹
resource "aws_security_group" "db" {
  name        = "${var.project_name}-${var.environment}-db-sg"
  description = "Security group for ${var.project_name} RDS in ${var.environment}"
  vpc_id      = var.vpc_id

  # ECS로부터의 MySQL 인바운드 트래픽 허용
  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs.id]
  }

  # 배스천 호스트로부터의 MySQL 인바운드 트래픽 허용
  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = var.bastion_sg_id != "" ? [var.bastion_sg_id] : []
  }

  # 제한된 아웃바운드 트래픽 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-db-sg"
    Environment = var.environment
  }
}

# Redis 보안 그룹
resource "aws_security_group" "redis" {
  name        = "${var.project_name}-${var.environment}-redis-sg"
  description = "Security group for ${var.project_name} ElastiCache Redis in ${var.environment}"
  vpc_id      = var.vpc_id

  # ECS로부터의 Redis 인바운드 트래픽 허용
  ingress {
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs.id]
  }

  # 제한된 아웃바운드 트래픽 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-redis-sg"
    Environment = var.environment
  }
}

# 배스천 호스트 보안 그룹 (필요한 경우에만 생성)
resource "aws_security_group" "bastion" {
  count = var.create_bastion ? 1 : 0
  
  name        = "${var.project_name}-bastion-sg"
  description = "Security group for ${var.project_name} Bastion Host"
  vpc_id      = var.vpc_id

  # 관리자 IP로부터의 SSH 인바운드 트래픽 허용
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = var.admin_cidrs
  }

  # 모든 아웃바운드 트래픽 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-bastion-sg"
    Environment = var.environment
  }
}

# ECS 태스크 실행 역할
resource "aws_iam_role" "ecs_execution" {
  name = "${var.project_name}-${var.environment}-ecs-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name        = "${var.project_name}-${var.environment}-ecs-execution-role"
    Environment = var.environment
  }
}

# ECS 태스크 실행 역할에 정책 연결
resource "aws_iam_role_policy_attachment" "ecs_execution" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# ECS 태스크 역할
resource "aws_iam_role" "ecs_task" {
  name = "${var.project_name}-${var.environment}-ecs-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name        = "${var.project_name}-${var.environment}-ecs-task-role"
    Environment = var.environment
  }
}

# S3 접근 정책
resource "aws_iam_policy" "s3_access" {
  name        = "${var.project_name}-${var.environment}-s3-access-policy"
  description = "S3 bucket access policy for ${var.project_name} in ${var.environment}"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:ListBucket",
          "s3:DeleteObject"
        ]
        Effect = "Allow"
        Resource = [
          "arn:aws:s3:::${var.s3_bucket_name}",
          "arn:aws:s3:::${var.s3_bucket_name}/*"
        ]
      }
    ]
  })
}

# ECS 태스크 역할에 S3 접근 정책 연결
resource "aws_iam_role_policy_attachment" "s3_access" {
  role       = aws_iam_role.ecs_task.name
  policy_arn = aws_iam_policy.s3_access.arn
}

# FirebaseService 접근 정책
resource "aws_iam_policy" "firebase_access" {
  name        = "${var.project_name}-${var.environment}-firebase-access-policy"
  description = "Firebase access policy for ${var.project_name} in ${var.environment}"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "ssm:GetParameter",
          "ssm:GetParameters"
        ]
        Effect = "Allow"
        Resource = [
          "arn:aws:ssm:*:*:parameter/${var.project_name}/${var.environment}/firebase*"
        ]
      }
    ]
  })
}

# ECS 태스크 역할에 Firebase 접근 정책 연결
resource "aws_iam_role_policy_attachment" "firebase_access" {
  role       = aws_iam_role.ecs_task.name
  policy_arn = aws_iam_policy.firebase_access.arn
}

# Secrets Manager 접근 정책
resource "aws_iam_policy" "secrets_access" {
  name        = "${var.project_name}-${var.environment}-secrets-access-policy"
  description = "Secrets Manager access policy for ${var.project_name} in ${var.environment}"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "secretsmanager:GetSecretValue"
        ]
        Effect = "Allow"
        Resource = [
          "arn:aws:secretsmanager:*:*:secret:*"
        ]
      }
    ]
  })
}

# ECS 태스크 실행 역할에 Secrets Manager 접근 정책 연결
resource "aws_iam_role_policy_attachment" "secrets_access" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = aws_iam_policy.secrets_access.arn
}
