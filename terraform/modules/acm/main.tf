resource "aws_acm_certificate" "main" {
  domain_name       = var.environment == "dev" ? "dev.${var.domain_name}" : "prod.${var.domain_name}"
  validation_method = "DNS"

  tags = {
    Name        = "${var.environment}-certificate"
    Environment = var.environment
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_route53_record" "validation" {
  for_each = {
    for dvo in aws_acm_certificate.main.domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  }

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = var.route53_zone_id
}

resource "aws_acm_certificate_validation" "main" {
  certificate_arn         = aws_acm_certificate.main.arn
  validation_record_fqdns = [for record in aws_route53_record.validation : record.fqdn]
}

# HTTPS 리스너 추가
resource "aws_lb_listener" "https" {
  load_balancer_arn = var.load_balancer_arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = aws_acm_certificate_validation.main.certificate_arn

  default_action {
    type             = "forward"
    target_group_arn = var.target_group_arn
  }
}

# HTTP를 HTTPS로 리다이렉트 - 기존 리스너가 있으므로 제거
# 필요한 경우 AWS 콘솔에서 HTTP 리스너를 수동으로 HTTPS로 리다이렉트하도록 설정할 수 있습니다
