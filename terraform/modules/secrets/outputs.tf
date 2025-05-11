output "dev_db_username_arn" {
  description = "The ARN of the dev database username secret"
  value       = aws_secretsmanager_secret.dev_db_username.arn
}

output "dev_db_password_arn" {
  description = "The ARN of the dev database password secret"
  value       = aws_secretsmanager_secret.dev_db_password.arn
}

output "prod_db_username_arn" {
  description = "The ARN of the prod database username secret"
  value       = aws_secretsmanager_secret.prod_db_username.arn
}

output "prod_db_password_arn" {
  description = "The ARN of the prod database password secret"
  value       = aws_secretsmanager_secret.prod_db_password.arn
}

output "jwt_secret_arn" {
  description = "The ARN of the JWT secret"
  value       = aws_secretsmanager_secret.jwt_secret.arn
}

output "aws_access_key_arn" {
  description = "The ARN of the AWS access key secret"
  value       = aws_secretsmanager_secret.aws_access_key.arn
}

output "aws_secret_key_arn" {
  description = "The ARN of the AWS secret key secret"
  value       = aws_secretsmanager_secret.aws_secret_key.arn
}

output "aws_s3_bucket_arn" {
  description = "The ARN of the AWS S3 bucket name secret"
  value       = aws_secretsmanager_secret.aws_s3_bucket.arn
}

output "mail_username_arn" {
  description = "The ARN of the mail username secret"
  value       = aws_secretsmanager_secret.mail_username.arn
}

output "mail_password_arn" {
  description = "The ARN of the mail password secret"
  value       = aws_secretsmanager_secret.mail_password.arn
}