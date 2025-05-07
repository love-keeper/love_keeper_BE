#!/bin/bash
# 이 스크립트는 Terraform 원격 상태 저장소를 초기화합니다.
# 이미 존재하는 리소스는 건너뜁니다.

set -e

# AWS 계정이 설정되어 있는지 확인
if ! aws sts get-caller-identity > /dev/null 2>&1; then
  echo "Error: AWS 계정이 설정되어 있지 않습니다. aws configure를 실행하세요."
  exit 1
fi

# 현재 디렉토리 저장
CURRENT_DIR=$(pwd)

# 스크립트가 있는 디렉토리로 이동
cd "$(dirname "$0")/.."

echo "Terraform 원격 상태 저장소를 초기화합니다..."

# S3 버킷 확인 및 생성
BUCKET_NAME="lovekeeper-terraform-state"
if aws s3api head-bucket --bucket $BUCKET_NAME 2>/dev/null; then
  echo "S3 버킷 '$BUCKET_NAME'이 이미 존재합니다. 건너뜁니다."
else
  echo "S3 버킷 '$BUCKET_NAME' 생성 중..."
  aws s3api create-bucket \
    --bucket $BUCKET_NAME \
    --region ap-northeast-2 \
    --create-bucket-configuration LocationConstraint=ap-northeast-2
  echo "S3 버킷 생성 완료!"
fi

# 버전 관리 확인 및 활성화
VERSIONING=$(aws s3api get-bucket-versioning --bucket $BUCKET_NAME)
if [[ $VERSIONING == *"Enabled"* ]]; then
  echo "버킷 버전 관리가 이미 활성화되어 있습니다."
else
  echo "버전 관리 활성화 중..."
  aws s3api put-bucket-versioning \
    --bucket $BUCKET_NAME \
    --versioning-configuration Status=Enabled
  echo "버전 관리 활성화 완료!"
fi

# 서버 측 암호화 확인 및 활성화
ENCRYPTION=$(aws s3api get-bucket-encryption --bucket $BUCKET_NAME 2>/dev/null || echo "NotConfigured")
if [[ $ENCRYPTION != "NotConfigured" ]]; then
  echo "버킷 암호화가 이미 구성되어 있습니다."
else
  echo "서버 측 암호화 활성화 중..."
  aws s3api put-bucket-encryption \
    --bucket $BUCKET_NAME \
    --server-side-encryption-configuration '{
      "Rules": [
        {
          "ApplyServerSideEncryptionByDefault": {
            "SSEAlgorithm": "AES256"
          }
        }
      ]
    }'
  echo "서버 측 암호화 활성화 완료!"
fi

# 퍼블릭 액세스 차단 확인 및 설정
PUBLIC_ACCESS=$(aws s3api get-public-access-block --bucket $BUCKET_NAME 2>/dev/null || echo "NotConfigured")
if [[ $PUBLIC_ACCESS != "NotConfigured" ]]; then
  echo "퍼블릭 액세스 차단이 이미 구성되어 있습니다."
else
  echo "퍼블릭 액세스 차단 중..."
  aws s3api put-public-access-block \
    --bucket $BUCKET_NAME \
    --public-access-block-configuration "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
  echo "퍼블릭 액세스 차단 완료!"
fi

# DynamoDB 테이블 확인 및 생성
TABLE_NAME="terraform-state-lock"
if aws dynamodb describe-table --table-name $TABLE_NAME 2>/dev/null; then
  echo "DynamoDB 테이블 '$TABLE_NAME'이 이미 존재합니다. 건너뜁니다."
else
  echo "DynamoDB 테이블 '$TABLE_NAME' 생성 중..."
  aws dynamodb create-table \
    --table-name $TABLE_NAME \
    --attribute-definitions AttributeName=LockID,AttributeType=S \
    --key-schema AttributeName=LockID,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region ap-northeast-2
  echo "DynamoDB 테이블 생성 완료!"
fi

echo "Terraform 원격 상태 저장소 초기화가 완료되었습니다!"
echo ""
echo "설정된 리소스:"
echo "- S3 버킷: $BUCKET_NAME"
echo "- DynamoDB 테이블: $TABLE_NAME"
echo ""
echo "이제 terraform init 명령어를 사용하여 프로젝트를 초기화할 수 있습니다:"
echo ""
echo "terraform init \\"
echo "  -backend-config=\"bucket=$BUCKET_NAME\" \\"
echo "  -backend-config=\"key=your-env/terraform.tfstate\" \\"
echo "  -backend-config=\"region=ap-northeast-2\" \\"
echo "  -backend-config=\"dynamodb_table=$TABLE_NAME\" \\"
echo "  -backend-config=\"encrypt=true\""

# 원래 디렉토리로 돌아가기
cd "$CURRENT_DIR"
