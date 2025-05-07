#!/bin/bash

# AWS 리전 설정
export AWS_REGION="ap-northeast-2"

# 인증서 상태 확인 및 설정 스크립트
setup_environment() {
  local env=$1
  echo "Setting up $env environment..."
  
  # 테라폼 작업 디렉토리로 이동
  cd /Users/parkdongkyu/my_project/lovekeeper/terraform/environments/$env
  
  # 테라폼 초기화
  terraform init
  
  # 실행 계획 생성
  terraform plan -out=tfplan
  
  # 변경 사항 적용
  terraform apply tfplan
  
  # ACM 인증서 상태 확인
  cert_arn=$(terraform output -raw certificate_arn)
  
  if [ -n "$cert_arn" ]; then
    echo "ACM Certificate ARN: $cert_arn"
    aws acm describe-certificate --certificate-arn $cert_arn --query 'Certificate.{Status:Status,Domain:DomainName}' --output table
  else
    echo "No certificate ARN found. Certificate may not have been created yet."
  fi
}

# 명령줄 인수 확인
if [ "$1" == "dev" ] || [ "$1" == "release" ]; then
  setup_environment $1
else
  echo "Usage: $0 [dev|release]"
  echo "  dev     - Setup development environment"
  echo "  release - Setup release environment"
  exit 1
fi

echo "Setup complete!"
echo "Note: DNS validation can take up to 30 minutes to complete."
echo "      Check the ACM certificate status in the AWS console or use the following command:"
echo "      aws acm describe-certificate --certificate-arn \$(terraform output -raw certificate_arn) --query 'Certificate.Status' --output text"
