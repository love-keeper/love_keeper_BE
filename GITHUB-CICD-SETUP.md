# GitHub CI/CD 설정 가이드

이 문서는 GitHub Actions를 사용하여 AWS Elastic Beanstalk에 자동 배포하기 위한 CI/CD 설정 가이드입니다.

## 1. GitHub Secrets 설정

GitHub에 AWS 인증 정보를 안전하게 저장하기 위해 Secrets를 설정해야 합니다:

1. GitHub 저장소 페이지로 이동합니다.
2. 상단 메뉴에서 **Settings** 탭을 클릭합니다.
3. 왼쪽 사이드바에서 **Secrets and variables** > **Actions**를 선택합니다.
4. **New repository secret** 버튼을 클릭합니다.
5. 다음 두 개의 시크릿을 추가합니다:
   - `AWS_ACCESS_KEY_ID`: AWS IAM 사용자의 액세스 키 ID
   - `AWS_SECRET_ACCESS_KEY`: AWS IAM 사용자의 시크릿 액세스 키

> **중요**: AWS IAM 사용자는 Elastic Beanstalk 리소스에 접근할 수 있는 충분한 권한을 가지고 있어야 합니다.

## 2. GitHub 워크플로우 설정 확인

`.github/workflows/deploy-to-eb.yml` 파일에서 다음 정보를 확인하고 필요시 수정하세요:

- `branches`: 배포를 트리거할 브랜치 이름 (기본값: `main`)
- `application_name`: AWS Elastic Beanstalk 애플리케이션 이름
- `environment_name`: AWS Elastic Beanstalk 환경 이름
- `region`: AWS 리전 (기본값: `ap-northeast-2`)

## 3. IAM 사용자 권한 설정

CI/CD에 사용할 IAM 사용자는 다음 권한이 필요합니다:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "ElasticBeanstalkPermissions",
            "Effect": "Allow",
            "Action": [
                "elasticbeanstalk:CreateApplicationVersion",
                "elasticbeanstalk:DescribeApplicationVersions",
                "elasticbeanstalk:DescribeEnvironments",
                "elasticbeanstalk:DescribeEvents",
                "elasticbeanstalk:UpdateEnvironment",
                "elasticbeanstalk:ListPlatformVersions",
                "elasticbeanstalk:CheckDNSAvailability"
            ],
            "Resource": "*"
        },
        {
            "Sid": "S3Permissions",
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:GetObjectAcl",
                "s3:GetObjectVersion",
                "s3:PutObject",
                "s3:PutObjectAcl",
                "s3:DeleteObject",
                "s3:CreateBucket",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::elasticbeanstalk-*",
                "arn:aws:s3:::elasticbeanstalk-*/*"
            ]
        }
    ]
}
```

## 4. CI/CD 워크플로우 작동 방식

이 CI/CD 파이프라인은 다음 시나리오에서 실행됩니다:

1. `main` 브랜치에 직접 push가 발생할 때
2. `main` 브랜치로의 PR이 merge될 때

워크플로우는 다음 단계를 수행합니다:

1. 코드 체크아웃
2. JDK 17 설정
3. Gradle로 프로젝트 빌드
4. 배포 패키지 생성
5. AWS 인증 설정
6. Elastic Beanstalk에 배포

## 5. 문제 해결

- **빌드 실패**: GitHub Actions 로그를 확인하여 빌드 오류 디버깅
- **배포 실패**: AWS Elastic Beanstalk 콘솔에서 환경 이벤트 로그 확인
- **권한 문제**: IAM 사용자의 권한이 충분한지 확인

## 6. 추가 설정 (선택 사항)

### 테스트 추가

빌드 단계 전에 테스트를 실행하려면 워크플로우 파일에 다음 단계를 추가할 수 있습니다:

```yaml
- name: Run tests
  run: ./gradlew test
```

### 배포 알림

배포 성공/실패 시 Slack이나 이메일로 알림을 받으려면 워크플로우 파일에 알림 액션을 추가할 수 있습니다.

### 환경별 배포

개발/스테이징/프로덕션 환경별로 다른 브랜치에서 배포하려면 워크플로우 파일의 `on` 섹션을 수정하세요.
