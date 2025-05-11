output "alb_id" {
  description = "The ID of the ALB"
  value       = aws_lb.main.id
}

output "alb_dns_name" {
  description = "The DNS name of the ALB"
  value       = aws_lb.main.dns_name
}

output "target_group_dev_arn" {
  description = "The ARN of the dev target group"
  value       = aws_lb_target_group.dev.arn
}

output "target_group_prod_arn" {
  description = "The ARN of the prod target group"
  value       = aws_lb_target_group.prod.arn
}