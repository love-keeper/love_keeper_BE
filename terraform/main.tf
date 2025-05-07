terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  required_version = ">= 1.0"

  backend "s3" {
    bucket         = "lovekeeper-terraform-state"
    key            = "terraform.tfstate"
    region         = "ap-northeast-2"
    encrypt        = true
    dynamodb_table = "lovekeeper-terraform-locks"
  }
}

provider "aws" {
  region = "ap-northeast-2"
  # AWS CLI 프로필 사용 시
  # profile = "lovekeeper"
  
  # 명시적인 자격 증명 대신 환경 변수나 IAM 역할 사용 권장
  # AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY 환경 변수 활용
}
