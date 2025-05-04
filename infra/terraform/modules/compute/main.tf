/*
* LoveKeeper 컴퓨트 모듈
* ECS 클러스터, 서비스, ALB 등 컴퓨트 리소스를 정의합니다.
*/

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# CloudWatch 로그 그룹
resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/${var.project_name}-${var.environment}"
  retention_in_days = 30
  # 이미 존재하는 로그 그룹을 가져오기 위한 설정
  skip_destroy = true
  # 이미 존재하는 리소스를 테라폼으로 가져옴 (import)
  lifecycle {
    prevent_destroy = true
    ignore_changes = [name]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-logs"
    Environment = var.environment
  }
}

# ECR 리포지토리
resource "aws_ecr_repository" "app" {
  count = var.create_ecr ? 1 : 0
  
  name                 = "${var.project_name}-app"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "${var.project_name}-app"
    Environment = var.environment
  }
}

# ECS 클러스터
resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-${var.environment}"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-cluster"
    Environment = var.environment
  }
}

# ECS 태스크 정의
resource "aws_ecs_task_definition" "app" {
  family                   = "${var.project_name}-${var.environment}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.task_cpu
  memory                   = var.task_memory
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([
    {
      name      = "app"
      image     = var.use_ecr ? (var.create_ecr ? "${aws_ecr_repository.app[0].repository_url}:${var.image_tag}" : "${var.ecr_repository_url}:${var.image_tag}") : var.container_image
      essential = true
      
      portMappings = [
        {
          containerPort = var.container_port
          hostPort      = var.container_port
          protocol      = "tcp"
        }
      ]

      environment = concat(
        [
          {
            name  = "SPRING_PROFILES_ACTIVE"
            value = var.environment
          },
          {
            name  = "SPRING_DATASOURCE_URL"
            value = "jdbc:mysql://${var.db_endpoint}/${var.db_name}?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8"
          },
          {
            name  = "SPRING_DATASOURCE_USERNAME"
            value = var.db_username
          },
          {
            name  = "SPRING_REDIS_HOST"
            value = var.redis_endpoint
          },
          {
            name  = "SPRING_REDIS_PORT"
            value = tostring(var.redis_port)
          },
          {
            name  = "SERVER_PORT"
            value = tostring(var.container_port)
          }
        ],
        var.additional_environment_variables
      )

      secrets = [
        {
          name      = "SPRING_DATASOURCE_PASSWORD"
          valueFrom = var.db_password_arn
        },
        {
          name      = "JWT_SECRET"
          valueFrom = var.jwt_secret_arn
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
    }
  ])

  tags = {
    Name        = "${var.project_name}-${var.environment}-task"
    Environment = var.environment
  }
}

# ALB 대상 그룹
resource "aws_lb_target_group" "app" {
  name        = "${var.project_name}-${var.environment}-tg"
  port        = var.container_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    path                = var.health_check_path
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
    matcher             = "200-299"
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-tg"
    Environment = var.environment
  }
}

# ALB 리스너 규칙 (공유 ALB 사용 시)
resource "aws_lb_listener_rule" "app" {
  count = var.create_alb ? 0 : 1
  
  listener_arn = var.alb_listener_arn
  priority     = var.alb_listener_rule_priority

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }

  condition {
    host_header {
      values = [var.alb_host_header]
    }
  }
}

# ALB (새로 생성 시)
resource "aws_lb" "app" {
  count = var.create_alb ? 1 : 0
  
  name               = "${var.project_name}-${var.environment}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [var.alb_security_group_id]
  subnets            = var.public_subnet_ids

  enable_deletion_protection = var.environment == "release"

  tags = {
    Name        = "${var.project_name}-${var.environment}-alb"
    Environment = var.environment
  }
}

# ALB HTTP 리스너 (새로 생성 시)
resource "aws_lb_listener" "http" {
  count = var.create_alb ? 1 : 0
  
  load_balancer_arn = aws_lb.app[0].arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }
}

# HTTPS 리스너는 인증서가 준비되면 추가할 예정입니다.
# resource "aws_lb_listener" "https" {
#   count = var.create_alb ? 1 : 0
#   
#   load_balancer_arn = aws_lb.app[0].arn
#   port              = 443
#   protocol          = "HTTPS"
#   ssl_policy        = "ELBSecurityPolicy-2016-08"
#   certificate_arn   = var.ssl_certificate_arn
#
#   default_action {
#     type             = "forward"
#     target_group_arn = aws_lb_target_group.app.arn
#   }
# }

# ECS 서비스
resource "aws_ecs_service" "app" {
  name                              = "${var.project_name}-${var.environment}"
  cluster                           = aws_ecs_cluster.main.id
  task_definition                   = aws_ecs_task_definition.app.arn
  desired_count                     = var.desired_count
  launch_type                       = "FARGATE"
  health_check_grace_period_seconds = 120

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.ecs_security_group_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.app.arn
    container_name   = "app"
    container_port   = var.container_port
  }

  depends_on = [
    aws_lb_target_group.app,
    aws_lb_listener_rule.app,
    aws_lb_listener.http
  ]

  tags = {
    Name        = "${var.project_name}-${var.environment}-service"
    Environment = var.environment
  }
}

# 배스천 호스트 (필요한 경우에만 생성)
resource "aws_instance" "bastion" {
  count = var.create_bastion && var.bastion_key_name != "" ? 1 : 0

  ami                    = var.bastion_ami_id
  instance_type          = var.bastion_instance_type
  key_name               = var.bastion_key_name
  subnet_id              = var.public_subnet_ids[0]
  vpc_security_group_ids = [var.bastion_security_group_id]

  associate_public_ip_address = true

  root_block_device {
    volume_size           = 20
    volume_type           = "gp2"
    delete_on_termination = true
  }

  tags = {
    Name        = "${var.project_name}-bastion"
    Environment = var.environment
  }
}
