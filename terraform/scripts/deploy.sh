#!/bin/bash

# 무중단 배포를 위한 스크립트
# 사용법: ./deploy.sh <environment> <region>

set -e

# 파라미터 체크
if [ $# -lt 2 ]; then
  echo "Usage: $0 <environment> <region>"
  echo "Example: $0 dev ap-northeast-2"
  exit 1
fi

ENVIRONMENT=$1
REGION=$2
CLUSTER="${ENVIRONMENT}-cluster"
SERVICE="${ENVIRONMENT}-service"

echo "Starting deployment to $ENVIRONMENT environment in $REGION region..."

# 현재 실행 중인 작업 정의 가져오기
TASK_DEFINITION=$(aws ecs describe-task-definition \
  --task-definition $(aws ecs describe-services --cluster $CLUSTER --services $SERVICE --region $REGION | jq -r '.services[0].taskDefinition') \
  --region $REGION)

# 새 작업 정의 등록 - 컨테이너 이미지만 업데이트
NEW_TASK_DEFINITION=$(echo $TASK_DEFINITION | jq '.taskDefinition' | jq '.containerDefinitions[0].image = "'$ECR_REPOSITORY:$IMAGE_TAG'"' | jq 'del(.taskDefinitionArn, .revision, .status, .requiresAttributes, .compatibilities, .registeredAt, .registeredBy)')

aws ecs register-task-definition \
  --region $REGION \
  --cli-input-json "$(echo $NEW_TASK_DEFINITION)"

# 서비스 업데이트 - 새 작업 정의 사용
aws ecs update-service \
  --cluster $CLUSTER \
  --service $SERVICE \
  --task-definition $(aws ecs describe-task-definition --task-definition $(echo $NEW_TASK_DEFINITION | jq -r '.family') --region $REGION | jq -r '.taskDefinition.taskDefinitionArn') \
  --region $REGION

echo "Deployment in progress. Waiting for service to stabilize..."

# 서비스가 안정화될 때까지 대기
aws ecs wait services-stable \
  --cluster $CLUSTER \
  --services $SERVICE \
  --region $REGION

echo "Deployment completed successfully!"
