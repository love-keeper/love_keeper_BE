#!/bin/bash
set -e

# AWS 환경 변수 설정
# export AWS_ACCESS_KEY_ID="AKIAQ6NCIGGYT3EJEBQV"
# export AWS_SECRET_ACCESS_KEY="JmZKvn36U1GWjtKwGLMBeZhb2EqjH43T9qBqqkP"
# export AWS_REGION="ap-northeast-2"

# 또는 AWS 프로필 사용
# export AWS_PROFILE="lovekeeper-release"

# 릴리스 환경 디렉토리로 이동
cd "$(dirname "$0")/../environments/release"

# Terraform 초기화 및 적용
terraform init
terraform plan -var-file="secrets.tfvars"
terraform apply -var-file="secrets.tfvars" -auto-approve
