output "zone_id" {
  description = "ID of the Route53 zone"
  value       = aws_route53_zone.main.zone_id
}

output "zone_name" {
  description = "Name of the Route53 zone"
  value       = aws_route53_zone.main.name
}

output "domain_name" {
  description = "Domain name with environment prefix if applicable"
  value       = var.environment == "dev" ? "dev.${var.domain_name}" : "prod.${var.domain_name}"
}
