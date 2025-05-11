# Dev Database Credentials
resource "aws_secretsmanager_secret" "dev_db_username" {
  name        = "${var.project_name}/${var.environment}/dev/db/username"
  description = "Dev database username"
  
  tags = {
    Name        = "${var.project_name}-dev-db-username"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "dev_db_username" {
  secret_id     = aws_secretsmanager_secret.dev_db_username.id
  secret_string = var.dev_db_username
}

resource "aws_secretsmanager_secret" "dev_db_password" {
  name        = "${var.project_name}/${var.environment}/dev/db/password"
  description = "Dev database password"
  
  tags = {
    Name        = "${var.project_name}-dev-db-password"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "dev_db_password" {
  secret_id     = aws_secretsmanager_secret.dev_db_password.id
  secret_string = var.dev_db_password
}

# Prod Database Credentials
resource "aws_secretsmanager_secret" "prod_db_username" {
  name        = "${var.project_name}/${var.environment}/prod/db/username"
  description = "Prod database username"
  
  tags = {
    Name        = "${var.project_name}-prod-db-username"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "prod_db_username" {
  secret_id     = aws_secretsmanager_secret.prod_db_username.id
  secret_string = var.prod_db_username
}

resource "aws_secretsmanager_secret" "prod_db_password" {
  name        = "${var.project_name}/${var.environment}/prod/db/password"
  description = "Prod database password"
  
  tags = {
    Name        = "${var.project_name}-prod-db-password"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "prod_db_password" {
  secret_id     = aws_secretsmanager_secret.prod_db_password.id
  secret_string = var.prod_db_password
}

# JWT Secret
resource "aws_secretsmanager_secret" "jwt_secret" {
  name        = "${var.project_name}/${var.environment}/jwt/secret"
  description = "JWT secret key"
  
  tags = {
    Name        = "${var.project_name}-jwt-secret"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "jwt_secret" {
  secret_id     = aws_secretsmanager_secret.jwt_secret.id
  secret_string = var.jwt_secret
}

# AWS Access Key
resource "aws_secretsmanager_secret" "aws_access_key" {
  name        = "${var.project_name}/${var.environment}/aws/access_key"
  description = "AWS access key"
  
  tags = {
    Name        = "${var.project_name}-aws-access-key"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "aws_access_key" {
  secret_id     = aws_secretsmanager_secret.aws_access_key.id
  secret_string = var.aws_access_key
}

# AWS Secret Key
resource "aws_secretsmanager_secret" "aws_secret_key" {
  name        = "${var.project_name}/${var.environment}/aws/secret_key"
  description = "AWS secret key"
  
  tags = {
    Name        = "${var.project_name}-aws-secret-key"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "aws_secret_key" {
  secret_id     = aws_secretsmanager_secret.aws_secret_key.id
  secret_string = var.aws_secret_key
}

# S3 Bucket Name
resource "aws_secretsmanager_secret" "aws_s3_bucket" {
  name        = "${var.project_name}/${var.environment}/aws/s3_bucket"
  description = "AWS S3 bucket name"
  
  tags = {
    Name        = "${var.project_name}-aws-s3-bucket"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "aws_s3_bucket" {
  secret_id     = aws_secretsmanager_secret.aws_s3_bucket.id
  secret_string = var.aws_s3_bucket
}

# Mail Username
resource "aws_secretsmanager_secret" "mail_username" {
  name        = "${var.project_name}/${var.environment}/mail/username"
  description = "Mail username"
  
  tags = {
    Name        = "${var.project_name}-mail-username"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "mail_username" {
  secret_id     = aws_secretsmanager_secret.mail_username.id
  secret_string = var.mail_username
}

# Mail Password
resource "aws_secretsmanager_secret" "mail_password" {
  name        = "${var.project_name}/${var.environment}/mail/password"
  description = "Mail password"
  
  tags = {
    Name        = "${var.project_name}-mail-password"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "mail_password" {
  secret_id     = aws_secretsmanager_secret.mail_password.id
  secret_string = var.mail_password
}