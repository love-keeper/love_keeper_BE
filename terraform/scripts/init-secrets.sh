#!/bin/bash
set -e

# 환경 설정
ENV=$1
if [[ -z "$ENV" ]]; then
  echo "Usage: $0 <environment> (dev or release)"
  exit 1
fi

if [[ "$ENV" != "dev" && "$ENV" != "release" ]]; then
  echo "Environment must be 'dev' or 'release'"
  exit 1
fi

# AWS 설정 로드
source ~/.aws/credentials.env # AWS 자격 증명을 포함한 환경 변수 파일
export AWS_REGION="ap-northeast-2"

# 시크릿 파일 경로
SECRETS_FILE="../environments/$ENV/secrets.tfvars"

# 시크릿 파일 존재 여부 확인
if [[ ! -f "$SECRETS_FILE" ]]; then
  echo "Error: Secrets file $SECRETS_FILE not found"
  exit 1
fi

# AWS Secrets Manager에 시크릿 저장
# tfvars 파일 읽기 및 JSON으로 변환
JSON_CONTENT="{"
while IFS="=" read -r key value; do
  # 주석이나 빈 줄 건너뛰기
  [[ "$key" =~ ^[[:space:]]*# ]] && continue
  [[ -z "$key" ]] && continue
  
  # 앞뒤 공백과 따옴표 제거
  key=$(echo "$key" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
  value=$(echo "$value" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//' -e 's/^"//' -e 's/"$//')
  
  # JSON 형식에 맞게 추가
  [[ "$JSON_CONTENT" != "{" ]] && JSON_CONTENT="$JSON_CONTENT,"
  JSON_CONTENT="$JSON_CONTENT\"$key\":\"$value\""
done < "$SECRETS_FILE"
JSON_CONTENT="$JSON_CONTENT}"

# 시크릿 매니저에 저장
SECRET_ID="$ENV/lovekeeper/app-secrets"

# 시크릿이 이미 존재하는지 확인
if aws secretsmanager describe-secret --secret-id "$SECRET_ID" > /dev/null 2>&1; then
  # 존재하면 업데이트
  aws secretsmanager update-secret --secret-id "$SECRET_ID" --secret-string "$JSON_CONTENT"
  echo "Secret $SECRET_ID has been updated"
else
  # 존재하지 않으면 생성
  aws secretsmanager create-secret --name "$SECRET_ID" --description "Application secrets for $ENV environment" --secret-string "$JSON_CONTENT"
  echo "Secret $SECRET_ID has been created"
fi

echo "Secrets have been successfully stored in AWS Secrets Manager"
