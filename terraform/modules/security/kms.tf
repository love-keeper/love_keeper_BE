resource "aws_kms_key" "firebase" {
  description             = "${var.environment} Firebase service account key"
  deletion_window_in_days = 10
  enable_key_rotation     = true

  tags = {
    Name        = "${var.environment}-firebase-kms-key"
    Environment = var.environment
  }
}

resource "aws_kms_alias" "firebase" {
  name          = "alias/${var.environment}-firebase-kms-key"
  target_key_id = aws_kms_key.firebase.key_id
}

output "kms_key_id" {
  description = "ID of the KMS key for Firebase service account"
  value       = aws_kms_key.firebase.key_id
}

output "kms_key_arn" {
  description = "ARN of the KMS key for Firebase service account"
  value       = aws_kms_key.firebase.arn
}
