resource "aws_route53_zone" "main" {
  name = var.domain_name

  tags = {
    Name        = "${var.environment}-zone"
    Environment = var.environment
  }
}

resource "aws_route53_record" "www" {
  zone_id = aws_route53_zone.main.zone_id
  name    = var.environment == "dev" ? "dev.${var.domain_name}" : "prod.${var.domain_name}"
  type    = "A"

  alias {
    name                   = var.alb_dns_name
    zone_id                = var.alb_zone_id
    evaluate_target_health = true
  }
}
