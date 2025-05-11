# AWS Parameter Store 설정 가이드

이 문서는 AWS Parameter Store를 사용하여 환경 변수를 안전하게 관리하는 방법을 설명합니다.

## Parameter Store란?

AWS Systems Manager Parameter Store는 구성 데이터 관리 및 비밀 관리를 위한 안전한 계층적 스토리지를 제공합니다. AWS Secrets Manager보다 비용이 저렴하고 간단한 구성에 적합합니다.

## 구성 방식

본 프로젝트에서는 다음과 같은 계층 구조로 Parameter Store를 구성합니다:

```
/lovekeeper/dev-prod/dev/db/username
/lovekeeper/dev-prod/dev/db/password
/lovekeeper/dev-prod/prod/db/username
/lovekeeper/dev-prod/prod/db/password
/lovekeeper/dev-prod/jwt/secret
...
```

## 파라미터 유형

- **String**: 일반 텍스트 값 (비민감 정보)
- **SecureString**: 암호화된 값 (민감 정보)

## Terraform으로 Parameter Store 설정하기

1. `terraform/modules/parameter-store` 모듈을 사용하여 Parameter Store 파라미터를 생성
2. `terraform.tfvars` 파일에 실제 값 설정
3. `terraform apply` 실행

## ECS 작업 정의에서 Parameter Store 사용

ECS 작업 정의에서는 다음과 같이 Parameter Store의 값을 참조합니다:

```json
"secrets": [
  {
    "name": "DEV_DB_USERNAME",
    "valueFrom": "/lovekeeper/dev-prod/dev/db/username"
  }
]
```

## IAM 권한 설정

ECS 작업 실행 역할(Execution Role)에 다음 권한이 필요합니다:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "ssm:GetParameters",
        "ssm:GetParameter",
        "kms:Decrypt"
      ],
      "Effect": "Allow",
      "Resource": [
        "arn:aws:ssm:ap-northeast-2:*:parameter/lovekeeper/*"
      ]
    }
  ]
}
```

## GitHub Actions 설정

GitHub Actions에서 사용할 경우, 다음 시크릿을 설정해야 합니다:

- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `FIREBASE_CREDENTIALS`

## 수동으로 Parameter Store 파라미터 관리하기

AWS Console 또는 CLI를 통해 파라미터를 수동으로 관리할 수 있습니다:

### AWS CLI 예시

```bash
# 파라미터 생성
aws ssm put-parameter \
    --name "/lovekeeper/dev-prod/jwt/secret" \
    --value "your-secret-value" \
    --type "SecureString" \
    --description "JWT Secret Key"

# 파라미터 값 조회
aws ssm get-parameter \
    --name "/lovekeeper/dev-prod/jwt/secret" \
    --with-decryption

# 파라미터 값 업데이트
aws ssm put-parameter \
    --name "/lovekeeper/dev-prod/jwt/secret" \
    --value "new-secret-value" \
    --type "SecureString" \
    --overwrite
```

## 장점

1. **비용 효율성**: Secrets Manager보다 저렴한 비용
2. **간편한 접근 제어**: IAM 정책을 통한 세밀한 접근 제어
3. **계층적 구조**: 경로 기반의 계층적 구조로 파라미터 관리
4. **암호화**: KMS를 통한 암호화 지원
5. **버전 관리**: 파라미터 변경 이력 추적 가능