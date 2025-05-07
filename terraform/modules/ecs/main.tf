resource "aws_ecr_repository" "main" {
  name                 = "${var.environment}-lovekeeper"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "${var.environment}-lovekeeper-ecr"
    Environment = var.environment
  }
}

resource "aws_ecs_cluster" "main" {
  name = "${var.environment}-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name        = "${var.environment}-ecs-cluster"
    Environment = var.environment
  }
}

resource "aws_ecs_task_definition" "main" {
  family             = "${var.environment}-task"
  network_mode       = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                = var.task_cpu
  memory             = var.task_memory
  execution_role_arn = aws_iam_role.ecs_execution_role.arn
  task_role_arn      = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "${var.environment}-container"
      image     = "${aws_ecr_repository.main.repository_url}:latest"
      essential = true
      portMappings = [
        {
          containerPort = 8082
          hostPort      = 8082
          protocol      = "tcp"
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.ecs.name
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = "ecs"
        }
      }
      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = var.environment }
      ]
      secrets = [
        { name = "DEV_DB_HOST", valueFrom = "/${var.environment}/db/host" },
        { name = "DEV_DB_USERNAME", valueFrom = "/${var.environment}/db/username" },
        { name = "DEV_DB_PASSWORD", valueFrom = "/${var.environment}/db/password" },
        { name = "DEV_REDIS_HOST", valueFrom = "/${var.environment}/redis/host" },
        { name = "MAIL_USERNAME", valueFrom = "/${var.environment}/mail/username" },
        { name = "MAIL_PASSWORD", valueFrom = "/${var.environment}/mail/password" },
        { name = "JWT_SECRET", valueFrom = "/${var.environment}/jwt/secret" },
        { name = "AWS_ACCESS_KEY", valueFrom = "/${var.environment}/aws/access_key" },
        { name = "AWS_SECRET_KEY", valueFrom = "/${var.environment}/aws/secret_key" },
        { name = "AWS_S3_BUCKET", valueFrom = "/${var.environment}/aws/s3_bucket" },
        { name = "FCM_CREDENTIALS", valueFrom = "/${var.environment}/firebase/credentials" }
      ]
    }
  ])

  tags = {
    Name        = "${var.environment}-task-definition"
    Environment = var.environment
  }
}

resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/${var.environment}-lovekeeper"
  retention_in_days = 30

  tags = {
    Name        = "${var.environment}-log-group"
    Environment = var.environment
  }
}

resource "aws_ecs_service" "main" {
  name            = "${var.environment}-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.main.arn
  desired_count   = var.service_desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets = [var.private_subnet_1_id, var.private_subnet_2_id]
    security_groups = [var.ecs_security_group_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.target_group_arn
    container_name   = "${var.environment}-container"
    container_port   = 8082
  }

  depends_on = [
    aws_iam_role_policy_attachment.ecs_execution_role_policy
  ]

  tags = {
    Name        = "${var.environment}-ecs-service"
    Environment = var.environment
  }
}

# ECS 실행 역할
resource "aws_iam_role" "ecs_execution_role" {
  name = "${var.environment}-ecs-execution-role"

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
    Name        = "${var.environment}-ecs-execution-role"
    Environment = var.environment
  }
}

# ECS 태스크 역할
resource "aws_iam_role" "ecs_task_role" {
  name = "${var.environment}-ecs-task-role"

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
    Name        = "${var.environment}-ecs-task-role"
    Environment = var.environment
  }
}

# ECS 실행 역할 정책 연결
resource "aws_iam_role_policy_attachment" "ecs_execution_role_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# ECS 태스크 역할에 S3 접근 정책 연결
resource "aws_iam_policy" "s3_access" {
  name        = "${var.environment}-s3-access-policy"
  description = "Policy that allows access to S3"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:DeleteObject",
          "s3:ListBucket"
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

# SSM 파라미터 접근 정책
resource "aws_iam_policy" "ssm_access" {
  name        = "${var.environment}-ssm-access-policy"
  description = "Policy that allows access to SSM Parameters"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "ssm:GetParameters",
          "ssm:GetParameter"
        ]
        Effect   = "Allow"
        Resource = "arn:aws:ssm:${var.region}:${data.aws_caller_identity.current.account_id}:parameter/${var.environment}/*"
      }
    ]
  })
}

# Secrets Manager 접근 정책
resource "aws_iam_policy" "secrets_manager_access" {
  name        = "${var.environment}-secrets-manager-access-policy"
  description = "Policy that allows access to Secrets Manager"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "secretsmanager:GetSecretValue",
          "secretsmanager:DescribeSecret"
        ]
        Effect   = "Allow"
        Resource = "arn:aws:secretsmanager:${var.region}:${data.aws_caller_identity.current.account_id}:secret:${var.environment}/*"
      }
    ]
  })
}

# KMS 키 접근 정책 (AWS SSM Parameter Store에서 암호화된 값을 복호화하기 위함)
resource "aws_iam_policy" "kms_access" {
  name        = "${var.environment}-kms-access-policy"
  description = "Policy that allows access to KMS for decryption"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "kms:Decrypt"
        ]
        Effect   = "Allow"
        Resource = "*"
      }
    ]
  })
}

# 정책 연결
resource "aws_iam_role_policy_attachment" "task_s3" {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = aws_iam_policy.s3_access.arn
}

resource "aws_iam_role_policy_attachment" "execution_ssm" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = aws_iam_policy.ssm_access.arn
}

resource "aws_iam_role_policy_attachment" "execution_secrets_manager" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = aws_iam_policy.secrets_manager_access.arn
}

resource "aws_iam_role_policy_attachment" "task_secrets_manager" {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = aws_iam_policy.secrets_manager_access.arn
}

resource "aws_iam_role_policy_attachment" "execution_kms" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = aws_iam_policy.kms_access.arn
}

# 현재 AWS 계정 ID 조회
data "aws_caller_identity" "current" {}

# 기존 AWS Secrets Manager 시크릿 참조
data "aws_secretsmanager_secret" "app_secrets" {
  name = "${var.environment}/lovekeeper/app-secrets"
}

# 시크릿 값 업데이트
resource "aws_secretsmanager_secret_version" "app_secrets" {
  secret_id = data.aws_secretsmanager_secret.app_secrets.id
  secret_string = jsonencode({
    mail_username  = var.mail_username
    mail_password  = var.mail_password
    jwt_secret     = var.jwt_secret
    aws_access_key = var.aws_access_key
    aws_secret_key = var.aws_secret_key
    s3_bucket_name = var.s3_bucket_name
  })
}

# 환경 변수를 AWS SSM Parameter Store에도 저장 (ECS 컨테이너 정의와의 호환성을 위해)
resource "aws_ssm_parameter" "mail_username" {
  name        = "/${var.environment}/mail/username"
  description = "Mail username for ${var.environment} environment"
  type        = "SecureString"
  value       = var.mail_username

  tags = {
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "mail_password" {
  name        = "/${var.environment}/mail/password"
  description = "Mail password for ${var.environment} environment"
  type        = "SecureString"
  value       = var.mail_password

  tags = {
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "jwt_secret" {
  name        = "/${var.environment}/jwt/secret"
  description = "JWT secret for ${var.environment} environment"
  type        = "SecureString"
  value       = var.jwt_secret

  tags = {
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "aws_access_key" {
  name        = "/${var.environment}/aws/access_key"
  description = "AWS access key for ${var.environment} environment"
  type        = "SecureString"
  value       = var.aws_access_key

  tags = {
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "aws_secret_key" {
  name        = "/${var.environment}/aws/secret_key"
  description = "AWS secret key for ${var.environment} environment"
  type        = "SecureString"
  value       = var.aws_secret_key

  tags = {
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "aws_s3_bucket" {
  name        = "/${var.environment}/aws/s3_bucket"
  description = "AWS S3 bucket for ${var.environment} environment"
  type        = "String"
  value       = var.s3_bucket_name

  tags = {
    Environment = var.environment
  }
}

# S3 버킷 생성
resource "aws_s3_bucket" "main" {
  bucket = var.s3_bucket_name

  tags = {
    Name        = var.s3_bucket_name
    Environment = var.environment
  }
}

# S3 버킷 퍼블릭 액세스 차단
resource "aws_s3_bucket_public_access_block" "main" {
  bucket = aws_s3_bucket.main.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# S3 버킷 암호화 설정
resource "aws_s3_bucket_server_side_encryption_configuration" "main" {
  bucket = aws_s3_bucket.main.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# Firebase 자격 증명을 AWS Secrets Manager에 저장
resource "aws_secretsmanager_secret" "firebase_credentials" {
  name        = "${var.environment}/lovekeeper/firebase-credentials"
  description = "Firebase service account credentials for ${var.environment} environment"
  
  tags = {
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "firebase_credentials" {
  secret_id     = aws_secretsmanager_secret.firebase_credentials.id
  secret_string = var.firebase_credentials
}

# Firebase 자격 증명을 SSM Parameter Store에도 저장
resource "aws_ssm_parameter" "firebase_credentials" {
  name        = "/${var.environment}/firebase/credentials"
  description = "Firebase service account credentials for ${var.environment} environment"
  type        = "SecureString"
  value       = var.firebase_credentials

  tags = {
    Environment = var.environment
  }
}
