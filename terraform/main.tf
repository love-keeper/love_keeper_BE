provider "aws" {
  region = var.aws_region
}

# Create a VPC
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.project_name}-vpc"
  }
}

# Create Internet Gateway
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-igw"
  }
}

# Public Subnets
resource "aws_subnet" "public_subnet_1" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnet_1_cidr
  availability_zone       = "${var.aws_region}a"
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.project_name}-public-subnet-1"
  }
}

resource "aws_subnet" "public_subnet_2" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnet_2_cidr
  availability_zone       = "${var.aws_region}c"
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.project_name}-public-subnet-2"
  }
}

# Private Subnets
resource "aws_subnet" "private_subnet_dev" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.private_subnet_dev_cidr
  availability_zone = "${var.aws_region}a"

  tags = {
    Name = "${var.project_name}-private-subnet-dev"
  }
}

resource "aws_subnet" "private_subnet_release" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.private_subnet_release_cidr
  availability_zone = "${var.aws_region}c"

  tags = {
    Name = "${var.project_name}-private-subnet-release"
  }
}

# NAT Gateway (단일 NAT Gateway 사용으로 변경)
resource "aws_eip" "nat_1" {
  domain = "vpc"

  tags = {
    Name = "${var.project_name}-nat-eip-1"
  }
}

resource "aws_nat_gateway" "nat_gateway_1" {
  allocation_id = aws_eip.nat_1.id
  subnet_id     = aws_subnet.public_subnet_1.id

  tags = {
    Name = "${var.project_name}-nat-gateway-1"
  }

  depends_on = [aws_internet_gateway.igw]
}

# Route Tables
resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "${var.project_name}-public-route-table"
  }
}

resource "aws_route_table" "private_route_table_dev" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat_gateway_1.id
  }

  tags = {
    Name = "${var.project_name}-private-route-table-dev"
  }
}

resource "aws_route_table" "private_route_table_release" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat_gateway_1.id
  }

  tags = {
    Name = "${var.project_name}-private-route-table-release"
  }
}

# Route Table Associations
resource "aws_route_table_association" "public_1" {
  subnet_id      = aws_subnet.public_subnet_1.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_route_table_association" "public_2" {
  subnet_id      = aws_subnet.public_subnet_2.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_route_table_association" "private_dev" {
  subnet_id      = aws_subnet.private_subnet_dev.id
  route_table_id = aws_route_table.private_route_table_dev.id
}

resource "aws_route_table_association" "private_release" {
  subnet_id      = aws_subnet.private_subnet_release.id
  route_table_id = aws_route_table.private_route_table_release.id
}

# Security Groups
resource "aws_security_group" "alb_sg" {
  name        = "${var.project_name}-alb-sg"
  description = "Security group for ALB"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port = 80
    to_port   = 80
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 443
    to_port   = 443
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-alb-sg"
  }
}

resource "aws_security_group" "bastion_sg" {
  name        = "${var.project_name}-bastion-sg"
  description = "Security group for bastion host"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = var.allowed_ip_ranges
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-bastion-sg"
  }
}

resource "aws_security_group" "app_sg" {
  name        = "${var.project_name}-app-sg"
  description = "Security group for application servers"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port = 8080
    to_port   = 8080
    protocol  = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }

  ingress {
    from_port = 5000
    to_port   = 5000
    protocol  = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-app-sg"
  }
}

resource "aws_security_group" "db_sg" {
  name        = "${var.project_name}-db-sg"
  description = "Security group for database"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port = 3306
    to_port   = 3306
    protocol  = "tcp"
    security_groups = [aws_security_group.app_sg.id, aws_security_group.bastion_sg.id]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-db-sg"
  }
}

resource "aws_security_group" "redis_sg" {
  name        = "${var.project_name}-redis-sg"
  description = "Security group for Redis"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port = 6379
    to_port   = 6379
    protocol  = "tcp"
    security_groups = [aws_security_group.app_sg.id]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-redis-sg"
  }
}

# ECR Repository
resource "aws_ecr_repository" "app_repo" {
  name                 = var.project_name
  image_tag_mutability = "MUTABLE"
  force_delete         = true

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "${var.project_name}-ecr-repo"
  }
}

# S3 Bucket
resource "aws_s3_bucket" "app_bucket" {
  bucket = var.s3_bucket_name

  tags = {
    Name = "${var.project_name}-s3-bucket"
  }
}

resource "aws_s3_bucket_ownership_controls" "app_bucket_ownership" {
  bucket = aws_s3_bucket.app_bucket.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "app_bucket_access" {
  bucket = aws_s3_bucket.app_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# RDS - MySQL for Dev
resource "aws_db_subnet_group" "db_subnet_group_dev" {
  name = "${var.project_name}-db-subnet-group-dev"
  subnet_ids = [aws_subnet.private_subnet_dev.id, aws_subnet.private_subnet_release.id]

  tags = {
    Name = "${var.project_name}-db-subnet-group-dev"
  }
}

resource "aws_db_instance" "mysql_dev" {
  identifier           = "${var.project_name}-mysql-dev"
  allocated_storage    = 20
  storage_type         = "gp2"
  engine               = "mysql"
  engine_version       = "8.0"
  instance_class       = "db.t3.micro"
  db_name              = "love_keeper"
  username             = var.db_username
  password             = var.db_password
  parameter_group_name = "default.mysql8.0"
  db_subnet_group_name = aws_db_subnet_group.db_subnet_group_dev.name
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  skip_final_snapshot  = true
  multi_az             = false
  publicly_accessible  = false

  tags = {
    Name = "${var.project_name}-mysql-dev"
  }
}

# RDS - MySQL for Release
resource "aws_db_subnet_group" "db_subnet_group_release" {
  name = "${var.project_name}-db-subnet-group-release"
  subnet_ids = [aws_subnet.private_subnet_dev.id, aws_subnet.private_subnet_release.id]

  tags = {
    Name = "${var.project_name}-db-subnet-group-release"
  }
}

resource "aws_db_instance" "mysql_release" {
  identifier           = "${var.project_name}-mysql-release"
  allocated_storage    = 50
  storage_type         = "gp2"
  engine               = "mysql"
  engine_version       = "8.0"
  instance_class       = "db.t3.small"
  db_name              = "love_keeper"
  username             = var.db_username
  password             = var.db_password
  parameter_group_name = "default.mysql8.0"
  db_subnet_group_name = aws_db_subnet_group.db_subnet_group_release.name
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  skip_final_snapshot  = true
  multi_az             = true
  publicly_accessible  = false

  tags = {
    Name = "${var.project_name}-mysql-release"
  }
}

# ElastiCache Redis - Dev
resource "aws_elasticache_subnet_group" "redis_subnet_group_dev" {
  name = "${var.project_name}-redis-subnet-group-dev"
  subnet_ids = [aws_subnet.private_subnet_dev.id, aws_subnet.private_subnet_release.id]

  tags = {
    Name = "${var.project_name}-redis-subnet-group-dev"
  }
}

resource "aws_elasticache_cluster" "redis_dev" {
  cluster_id           = "${var.project_name}-redis-dev"
  engine               = "redis"
  node_type            = "cache.t3.micro"
  num_cache_nodes      = 1
  parameter_group_name = "default.redis6.x"
  engine_version       = "6.2"
  port                 = 6379
  subnet_group_name    = aws_elasticache_subnet_group.redis_subnet_group_dev.name
  security_group_ids = [aws_security_group.redis_sg.id]

  tags = {
    Name = "${var.project_name}-redis-dev"
  }
}

# ElastiCache Redis - Release
resource "aws_elasticache_subnet_group" "redis_subnet_group_release" {
  name = "${var.project_name}-redis-subnet-group-release"
  subnet_ids = [aws_subnet.private_subnet_dev.id, aws_subnet.private_subnet_release.id]

  tags = {
    Name = "${var.project_name}-redis-subnet-group-release"
  }
}

resource "aws_elasticache_replication_group" "redis_release" {
  replication_group_id       = "${var.project_name}-redis-release"
  description                = "Redis cluster for Release environment"
  node_type                  = "cache.t3.small"
  port                       = 6379
  parameter_group_name       = "default.redis6.x"
  automatic_failover_enabled = true
  engine_version             = "6.2"
  subnet_group_name          = aws_elasticache_subnet_group.redis_subnet_group_release.name
  security_group_ids = [aws_security_group.redis_sg.id]

  num_node_groups         = 1
  replicas_per_node_group = 1

  tags = {
    Name = "${var.project_name}-redis-release"
  }
}

# EC2 Bastion Host
# EC2 Bastion Host
resource "aws_instance" "bastion" {
  ami           = var.bastion_ami
  instance_type = "t3.micro"
  key_name      = var.key_name
  subnet_id     = aws_subnet.public_subnet_1.id
  vpc_security_group_ids = [aws_security_group.bastion_sg.id]

  tags = {
    Name = "${var.project_name}-bastion"
  }
}

# Application Load Balancer
resource "aws_lb" "app_lb" {
  name               = "${var.project_name}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups = [aws_security_group.alb_sg.id]
  subnets = [aws_subnet.public_subnet_1.id, aws_subnet.public_subnet_2.id]

  enable_deletion_protection = false

  tags = {
    Name = "${var.project_name}-alb"
  }
}

# ALB Target Groups
resource "aws_lb_target_group" "dev_tg" {
  name        = "${var.project_name}-dev-tg-new"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path                = "/actuator/health"
    port                = "traffic-port"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    matcher             = "200"
  }

  # Target Group이 리스너에 연결되어 있을 때 안전하게 교체 실행
  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name = "${var.project_name}-dev-tg"
  }
}

resource "aws_lb_target_group" "release_tg" {
  name        = "${var.project_name}-release-tg-8080"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path                = "/actuator/health"
    port                = "traffic-port"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    matcher             = "200"
  }

  # Target Group이 리스너에 연결되어 있을 때 안전하게 교체 실행
  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name = "${var.project_name}-release-tg"
  }
}

# ALB Listeners
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.app_lb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "redirect"
    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

# Note: In a real production environment, you should include SSL certificate
resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.app_lb.arn
  port              = 443
  protocol          = "HTTP" # Change to HTTPS with certificate in production

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.release_tg.arn
  }
}

# ALB Listener Rules
resource "aws_lb_listener_rule" "dev_rule" {
  listener_arn = aws_lb_listener.https.arn
  priority     = 10

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.dev_tg.arn
  }

  condition {
    host_header {
      values = ["dev.${var.domain_name}"]
    }
  }
}

resource "aws_lb_listener_rule" "release_rule" {
  listener_arn = aws_lb_listener.https.arn
  priority     = 20

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.release_tg.arn
  }

  condition {
    host_header {
      values = [var.domain_name]
    }
  }
}

# ECS Cluster - Dev
resource "aws_ecs_cluster" "ecs_cluster_dev" {
  name = "${var.project_name}-ecs-cluster-dev"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name = "${var.project_name}-ecs-cluster-dev"
  }
}

# ECS Cluster - Release
resource "aws_ecs_cluster" "ecs_cluster_release" {
  name = "${var.project_name}-ecs-cluster-release"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name = "${var.project_name}-ecs-cluster-release"
  }
}

# IAM Role for ECS Tasks
resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${var.project_name}-ecs-task-execution-role"

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
    Name = "${var.project_name}-ecs-task-execution-role"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# IAM Role for ECS Tasks - Additional Permissions
resource "aws_iam_role" "ecs_task_role" {
  name = "${var.project_name}-ecs-task-role"

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
    Name = "${var.project_name}-ecs-task-role"
  }
}

resource "aws_iam_policy" "ecs_task_s3_policy" {
  name        = "${var.project_name}-ecs-task-s3-policy"
  description = "Policy for ECS tasks to access S3"

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
          "${aws_s3_bucket.app_bucket.arn}",
          "${aws_s3_bucket.app_bucket.arn}/*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_s3_policy_attachment" {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = aws_iam_policy.ecs_task_s3_policy.arn
}

resource "aws_iam_policy" "ssm_parameter_store_policy" {
  name        = "${var.project_name}-ssm-parameter-store-policy"
  description = "Policy to allow access to SSM Parameter Store"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "ssm:GetParameters",
          "ssm:GetParameter",
          "ssm:GetParametersByPath"
        ]
        Effect   = "Allow"
        Resource = "arn:aws:ssm:${var.aws_region}:${data.aws_caller_identity.current.account_id}:parameter/${var.project_name}/*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_parameter_store_policy_attachment" {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = aws_iam_policy.ssm_parameter_store_policy.arn
}

# SSM Parameters - Dev Environment
resource "aws_ssm_parameter" "dev_db_host" {
  name      = "/${var.project_name}/dev/DB_HOST"
  type      = "String"
  value     = aws_db_instance.mysql_dev.address
  overwrite = true
}

resource "aws_ssm_parameter" "dev_db_username" {
  name      = "/${var.project_name}/dev/DB_USERNAME"
  type      = "SecureString"
  value     = var.db_username
  overwrite = true
}

resource "aws_ssm_parameter" "dev_db_password" {
  name      = "/${var.project_name}/dev/DB_PASSWORD"
  type      = "SecureString"
  value     = var.db_password
  overwrite = true
}

resource "aws_ssm_parameter" "dev_redis_host" {
  name      = "/${var.project_name}/dev/REDIS_HOST"
  type      = "String"
  value     = aws_elasticache_cluster.redis_dev.cache_nodes[0].address
  overwrite = true
}

resource "aws_ssm_parameter" "mail_username" {
  name      = "/${var.project_name}/MAIL_USERNAME"
  type      = "SecureString"
  value     = var.mail_username
  overwrite = true
}

resource "aws_ssm_parameter" "mail_password" {
  name      = "/${var.project_name}/MAIL_PASSWORD"
  type      = "SecureString"
  value     = var.mail_password
  overwrite = true
}

resource "aws_ssm_parameter" "jwt_secret" {
  name      = "/${var.project_name}/JWT_SECRET"
  type      = "SecureString"
  value     = var.jwt_secret
  overwrite = true
}

resource "aws_ssm_parameter" "aws_access_key" {
  name      = "/${var.project_name}/dev/AWS_ACCESS_KEY"
  type      = "SecureString"
  value     = var.aws_access_key
  overwrite = true
}

resource "aws_ssm_parameter" "aws_secret_key" {
  name      = "/${var.project_name}/dev/AWS_SECRET_KEY"
  type      = "SecureString"
  value     = var.aws_secret_key
  overwrite = true
}

resource "aws_ssm_parameter" "aws_s3_bucket" {
  name      = "/${var.project_name}/dev/AWS_S3_BUCKET"
  type      = "String"
  value     = aws_s3_bucket.app_bucket.bucket
  overwrite = true
}

# SSM Parameters - Release Environment
resource "aws_ssm_parameter" "prod_db_host" {
  name      = "/${var.project_name}/prod/DB_HOST"
  type      = "String"
  value     = aws_db_instance.mysql_release.address
  overwrite = true
}

resource "aws_ssm_parameter" "prod_db_username" {
  name      = "/${var.project_name}/prod/DB_USERNAME"
  type      = "SecureString"
  value     = var.db_username
  overwrite = true
}

resource "aws_ssm_parameter" "prod_db_password" {
  name      = "/${var.project_name}/prod/DB_PASSWORD"
  type      = "SecureString"
  value     = var.db_password
  overwrite = true
}

resource "aws_ssm_parameter" "prod_redis_host" {
  name      = "/${var.project_name}/prod/REDIS_HOST"
  type      = "String"
  value     = aws_elasticache_replication_group.redis_release.primary_endpoint_address
  overwrite = true
}

resource "aws_ssm_parameter" "prod_aws_access_key" {
  name      = "/${var.project_name}/prod/AWS_ACCESS_KEY"
  type      = "SecureString"
  value     = var.aws_access_key
  overwrite = true
}

resource "aws_ssm_parameter" "prod_aws_secret_key" {
  name      = "/${var.project_name}/prod/AWS_SECRET_KEY"
  type      = "SecureString"
  value     = var.aws_secret_key
  overwrite = true
}

resource "aws_ssm_parameter" "prod_aws_s3_bucket" {
  name      = "/${var.project_name}/prod/AWS_S3_BUCKET"
  type      = "String"
  value     = aws_s3_bucket.app_bucket.bucket
  overwrite = true
}

# ECS Task Definition - Dev
resource "aws_ecs_task_definition" "app_task_dev" {
  family             = "${var.project_name}-task-dev"
  network_mode       = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                = "512"
  memory             = "1024"
  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn      = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "${var.project_name}-container-dev"
      image     = "${aws_ecr_repository.app_repo.repository_url}:dev"
      essential = true

      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = "dev"
        }
      ]

      secrets = [
        {
          name      = "DEV_DB_HOST"
          valueFrom = aws_ssm_parameter.dev_db_host.arn
        },
        {
          name      = "DEV_DB_USERNAME"
          valueFrom = aws_ssm_parameter.dev_db_username.arn
        },
        {
          name      = "DEV_DB_PASSWORD"
          valueFrom = aws_ssm_parameter.dev_db_password.arn
        },
        {
          name      = "DEV_REDIS_HOST"
          valueFrom = aws_ssm_parameter.dev_redis_host.arn
        },
        {
          name      = "MAIL_USERNAME"
          valueFrom = aws_ssm_parameter.mail_username.arn
        },
        {
          name      = "MAIL_PASSWORD"
          valueFrom = aws_ssm_parameter.mail_password.arn
        },
        {
          name      = "JWT_SECRET"
          valueFrom = aws_ssm_parameter.jwt_secret.arn
        },
        {
          name      = "AWS_ACCESS_KEY"
          valueFrom = aws_ssm_parameter.aws_access_key.arn
        },
        {
          name      = "AWS_SECRET_KEY"
          valueFrom = aws_ssm_parameter.aws_secret_key.arn
        },
        {
          name      = "AWS_S3_BUCKET"
          valueFrom = aws_ssm_parameter.aws_s3_bucket.arn
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/${var.project_name}-dev"
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])

  tags = {
    Name = "${var.project_name}-task-dev"
  }
}

# ECS Task Definition - Release
resource "aws_ecs_task_definition" "app_task_release" {
  family             = "${var.project_name}-task-release"
  network_mode       = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                = "1024"
  memory             = "2048"
  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn      = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "${var.project_name}-container-release"
      image     = "${aws_ecr_repository.app_repo.repository_url}:release"
      essential = true

      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = "prod"
        }
      ]

      secrets = [
        {
          name      = "PROD_DB_HOST"
          valueFrom = aws_ssm_parameter.prod_db_host.arn
        },
        {
          name      = "PROD_DB_USERNAME"
          valueFrom = aws_ssm_parameter.prod_db_username.arn
        },
        {
          name      = "PROD_DB_PASSWORD"
          valueFrom = aws_ssm_parameter.prod_db_password.arn
        },
        {
          name      = "PROD_REDIS_HOST"
          valueFrom = aws_ssm_parameter.prod_redis_host.arn
        },
        {
          name      = "MAIL_USERNAME"
          valueFrom = aws_ssm_parameter.mail_username.arn
        },
        {
          name      = "MAIL_PASSWORD"
          valueFrom = aws_ssm_parameter.mail_password.arn
        },
        {
          name      = "JWT_SECRET"
          valueFrom = aws_ssm_parameter.jwt_secret.arn
        },
        {
          name      = "PROD_AWS_ACCESS_KEY"
          valueFrom = aws_ssm_parameter.prod_aws_access_key.arn
        },
        {
          name      = "PROD_AWS_SECRET_KEY"
          valueFrom = aws_ssm_parameter.prod_aws_secret_key.arn
        },
        {
          name      = "PROD_AWS_S3_BUCKET"
          valueFrom = aws_ssm_parameter.prod_aws_s3_bucket.arn
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/${var.project_name}-release"
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])

  tags = {
    Name = "${var.project_name}-task-release"
  }
}

# CloudWatch Log Groups
resource "aws_cloudwatch_log_group" "ecs_logs_dev" {
  name              = "/ecs/${var.project_name}-dev"
  retention_in_days = 30

  tags = {
    Name = "${var.project_name}-logs-dev"
  }
}

resource "aws_cloudwatch_log_group" "ecs_logs_release" {
  name              = "/ecs/${var.project_name}-release"
  retention_in_days = 30

  tags = {
    Name = "${var.project_name}-logs-release"
  }
}

# ECS Services
resource "aws_ecs_service" "app_service_dev" {
  name            = "${var.project_name}-service-dev"
  cluster         = aws_ecs_cluster.ecs_cluster_dev.id
  task_definition = aws_ecs_task_definition.app_task_dev.arn
  desired_count   = 2
  launch_type     = "FARGATE"

  network_configuration {
    subnets = [aws_subnet.private_subnet_dev.id]
    security_groups = [aws_security_group.app_sg.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.dev_tg.arn
    container_name   = "${var.project_name}-container-dev"
    container_port   = 8080
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  depends_on = [aws_lb_listener.https]

  tags = {
    Name = "${var.project_name}-service-dev"
  }
}

resource "aws_ecs_service" "app_service_release" {
  name            = "${var.project_name}-service-release"
  cluster         = aws_ecs_cluster.ecs_cluster_release.id
  task_definition = aws_ecs_task_definition.app_task_release.arn
  desired_count   = 2
  launch_type     = "FARGATE"

  network_configuration {
    subnets = [aws_subnet.private_subnet_release.id]
    security_groups = [aws_security_group.app_sg.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.release_tg.arn
    container_name   = "${var.project_name}-container-release"
    container_port   = 8080
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  depends_on = [aws_lb_listener.https]

  tags = {
    Name = "${var.project_name}-service-release"
  }
}

# Get Current AWS Account ID
data "aws_caller_identity" "current" {}

# IAM Role for GitHub Actions
resource "aws_iam_role" "github_actions_role" {
  name = "${var.project_name}-github-actions-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Federated = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:oidc-provider/token.actions.githubusercontent.com"
        }
        Condition = {
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
          }
          StringLike = {
            "token.actions.githubusercontent.com:sub" = "repo:${var.github_repo}:*"
          }
        }
      }
    ]
  })

  tags = {
    Name = "${var.project_name}-github-actions-role"
  }
}

# IAM Policy for GitHub Actions
resource "aws_iam_policy" "github_actions_policy" {
  name        = "${var.project_name}-github-actions-policy"
  description = "Policy for GitHub Actions to deploy to ECS"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability",
          "ecr:PutImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:GetAuthorizationToken"
        ]
        Effect   = "Allow"
        Resource = "*"
      },
      {
        Action = [
          "ecs:RegisterTaskDefinition",
          "ecs:DescribeTaskDefinition",
          "ecs:DescribeServices",
          "ecs:UpdateService"
        ]
        Effect   = "Allow"
        Resource = "*"
      },
      {
        Action = [
          "iam:PassRole"
        ]
        Effect = "Allow"
        Resource = [
          aws_iam_role.ecs_task_execution_role.arn,
          aws_iam_role.ecs_task_role.arn
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "github_actions_policy_attachment" {
  role       = aws_iam_role.github_actions_role.name
  policy_arn = aws_iam_policy.github_actions_policy.arn
}

# Outputs
output "alb_dns_name" {
  value       = aws_lb.app_lb.dns_name
  description = "The DNS name of the ALB"
}

output "ecr_repository_url" {
  value       = aws_ecr_repository.app_repo.repository_url
  description = "The URL of the ECR repository"
}

output "bastion_ip" {
  value       = aws_instance.bastion.public_ip
  description = "The public IP of the bastion host"
}

output "github_actions_role_arn" {
  value       = aws_iam_role.github_actions_role.arn
  description = "The ARN of the IAM role for GitHub Actions"
}
