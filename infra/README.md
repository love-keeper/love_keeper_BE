# LoveKeeper 인프라 구축 가이드

이 가이드는 LoveKeeper 애플리케이션의 AWS 인프라를 Terraform을 사용하여 구축하는 방법을 설명합니다.

## 아키텍처 구성

LoveKeeper 인프라는 다음 구성 요소로 이루어져 있습니다:

- VPC 및 서브넷(퍼블릭/프라이빗)
- Application Load Balancer
- ECS Fargate 클러스터
- RDS MySQL 데이터베이스
- ElastiCache Redis
- S3 버킷
- 보안 그룹 및 IAM 역할
- 선택적 배스천 호스트

## 사전 요구사항

- AWS CLI 설치 및 구성
- Terraform 설치 (v1.0.0 이상)
- AWS 계정 및 적절한 권한

## 배포 단계

### 1. AWS 인증 정보 설정

```bash
aws configure
```

### 2. 개발 환경 배포

```bash
cd terraform/environments/dev
terraform init
terraform plan -var-file=dev.tfvars
terraform apply -var-file=dev.tfvars
```

### 3. 개발 환경 출력값 확인

```bash
terraform output
```

### 4. 릴리스 환경 배포

개발 환경 ECR URL을 릴리스 환경 변수 파일에 추가해야 합니다:

```bash
cd ../release
terraform init
terraform plan -var-file=release.tfvars -var="ecr_repository_url=<개발_환경_ECR_URL>"
terraform apply -var-file=release.tfvars -var="ecr_repository_url=<개발_환경_ECR_URL>"
```

## GitHub Actions CI/CD 구성

LoveKeeper 애플리케이션은 GitHub Actions를 통한 CI/CD 파이프라인이 구성되어 있습니다:

1. GitHub 저장소 설정에서 다음 시크릿을 추가합니다:
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`
   - `SLACK_WEBHOOK` (선택사항)

2. 코드를 다음 브랜치에 푸시하면 자동 배포가 시작됩니다:
   - `develop`: 개발 환경으로 배포
   - `release`: 릴리스 환경으로 배포

## 인프라 관리

### 변경사항 적용

코드를 변경한 후 다음 명령을 실행합니다:

```bash
terraform plan
terraform apply
```

### 상태 확인

현재 인프라 상태를 확인합니다:

```bash
terraform state list
```

### 삭제

인프라를 삭제할 때는 주의해서 진행합니다:

```bash
terraform destroy
```

## 모듈 구조

- **networking**: VPC, 서브넷, 게이트웨이 등
- **security**: 보안 그룹, IAM 역할 등
- **database**: RDS, ElastiCache 등
- **compute**: ECS, ALB, EC2 등
- **storage**: S3 버킷 등

## 환경별 구성

- **dev**: 개발 및 테스트용 환경
- **release**: 프로덕션급 환경

## 문제 해결

일반적인 문제 및 해결 방법:

1. **Terraform 초기화 오류**: AWS 인증 정보를 확인하세요.
2. **배포 실패**: CloudWatch 로그에서 오류를 확인하세요.
3. **서비스 접근 불가**: 보안 그룹 설정을 확인하세요.

## 중요 참고사항

- 실제 배포 전에 민감한 정보(예: 데이터베이스 비밀번호)는 안전하게 관리하세요.
- 프로덕션 환경에서는 다중 AZ 구성을 사용하세요.
- 정기적으로 백업을 수행하세요.
