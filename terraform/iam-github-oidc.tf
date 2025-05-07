provider "aws" {
  region = "ap-northeast-2"
}

# GitHub OIDC 공급자 설정
resource "aws_iam_openid_connect_provider" "github" {
  url = "https://token.actions.githubusercontent.com"
  
  client_id_list = [
    "sts.amazonaws.com",
  ]
  
  thumbprint_list = [
    "6938fd4d98bab03faadb97b34396831e3780aea1" # GitHub Actions의 OIDC 인증서 지문
  ]
}

# GitHub Actions가 사용할 IAM 역할
resource "aws_iam_role" "github_actions" {
  name = "GitHubActionsOIDCRole"
  
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Federated = aws_iam_openid_connect_provider.github.arn
        },
        Action = "sts:AssumeRoleWithWebIdentity",
        Condition = {
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
          },
          StringLike = {
            "token.actions.githubusercontent.com:sub" = "repo:your-github-org/lovekeeper:*"
          }
        }
      }
    ]
  })
}

# 필요한 권한을 가진 정책 연결
resource "aws_iam_role_policy_attachment" "github_actions_permissions" {
  role       = aws_iam_role.github_actions.name
  policy_arn = "arn:aws:iam::aws:policy/AdministratorAccess" # 실제 운영에서는 더 제한된 권한을 사용하세요
}

# AWS Secrets Manager 접근 정책
resource "aws_iam_policy" "secrets_access" {
  name        = "GitHubActionsSecretsAccess"
  description = "Allow GitHub Actions to access Secrets Manager"
  
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "secretsmanager:GetSecretValue",
          "secretsmanager:DescribeSecret"
        ],
        Resource = [
          "arn:aws:secretsmanager:ap-northeast-2:*:secret:dev/*",
          "arn:aws:secretsmanager:ap-northeast-2:*:secret:release/*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "github_actions_secrets" {
  role       = aws_iam_role.github_actions.name
  policy_arn = aws_iam_policy.secrets_access.arn
}
