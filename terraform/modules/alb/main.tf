resource "aws_lb" "main" {
  name               = "${var.project_name}-${var.environment}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [var.alb_sg_id]
  subnets            = var.public_subnet_ids

  enable_deletion_protection = false  # Set to true for production

  tags = {
    Name        = "${var.project_name}-${var.environment}-alb"
    Project     = var.project_name
    Environment = var.environment
  }
}

# Target Group for Dev Environment
resource "aws_lb_target_group" "dev" {
  name     = "${var.project_name}-dev-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = var.vpc_id
  target_type = "ip"

  health_check {
    healthy_threshold   = 3
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/actuator/health"
    port                = "traffic-port"
    matcher             = "200"
  }

  tags = {
    Name        = "${var.project_name}-dev-tg"
    Project     = var.project_name
    Environment = "dev"
  }
}

# Target Group for Prod Environment
resource "aws_lb_target_group" "prod" {
  name     = "${var.project_name}-prod-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = var.vpc_id
  target_type = "ip"

  health_check {
    healthy_threshold   = 3
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/actuator/health"
    port                = "traffic-port"
    matcher             = "200"
  }

  tags = {
    Name        = "${var.project_name}-prod-tg"
    Project     = var.project_name
    Environment = "prod"
  }
}

# HTTP Listener (redirects to HTTPS in production)
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"
    fixed_response {
      content_type = "text/plain"
      message_body = "Please use the environment-specific paths."
      status_code  = "200"
    }
  }
}

# Rules for routing to dev and prod environments
resource "aws_lb_listener_rule" "dev" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 10

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.dev.arn
  }

  condition {
    path_pattern {
      values = ["/dev/*"]
    }
  }
}

resource "aws_lb_listener_rule" "prod" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 20

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.prod.arn
  }

  condition {
    path_pattern {
      values = ["/prod/*"]
    }
  }
}

# HTTPS Listener (For production, you should add SSL/TLS certificate)
# Uncomment and configure when ready for HTTPS
# resource "aws_lb_listener" "https" {
#   load_balancer_arn = aws_lb.main.arn
#   port              = 443
#   protocol          = "HTTPS"
#   ssl_policy        = "ELBSecurityPolicy-2016-08"
#   certificate_arn   = var.certificate_arn
#
#   default_action {
#     type = "fixed-response"
#     fixed_response {
#       content_type = "text/plain"
#       message_body = "Please use the environment-specific paths."
#       status_code  = "200"
#     }
#   }
# }
#
# resource "aws_lb_listener_rule" "https_dev" {
#   listener_arn = aws_lb_listener.https.arn
#   priority     = 10
#
#   action {
#     type             = "forward"
#     target_group_arn = aws_lb_target_group.dev.arn
#   }
#
#   condition {
#     path_pattern {
#       values = ["/dev/*"]
#     }
#   }
# }
#
# resource "aws_lb_listener_rule" "https_prod" {
#   listener_arn = aws_lb_listener.https.arn
#   priority     = 20
#
#   action {
#     type             = "forward"
#     target_group_arn = aws_lb_target_group.prod.arn
#   }
#
#   condition {
#     path_pattern {
#       values = ["/prod/*"]
#     }
#   }
# }