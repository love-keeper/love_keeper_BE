# AWS Elastic Beanstalk 환경 변수 설정 가이드

LoveKeeper 프로젝트를 ELB에 배포하기 위해 필요한 환경 변수 설정 가이드입니다.

## 필수 환경 변수

ELB 콘솔에서 다음 환경 변수들을 설정해주세요:

### 기본 환경 변수
- `PORT`: 8080 (이미 설정됨)
- `SPRING_PROFILES_ACTIVE`: elb

### 데이터베이스 관련
- `RDS_HOSTNAME`: 데이터베이스 호스트 (예: your-db-instance.xxxxxxxxxxxx.ap-northeast-2.rds.amazonaws.com)
- `RDS_PORT`: 데이터베이스 포트 (예: 3306)
- `RDS_DB_NAME`: 데이터베이스 이름 (예: love_keeper)
- `RDS_USERNAME`: 데이터베이스 사용자 이름
- `RDS_PASSWORD`: 데이터베이스 비밀번호

### Redis 관련
- `REDIS_HOST`: Redis 호스트 주소
- `REDIS_PORT`: Redis 포트 (기본값: 6379)

### JWT 관련
- `JWT_SECRET`: JWT 비밀키 (기존 application.yml에서 사용하던 값)

### AWS S3 관련
- `AWS_ACCESS_KEY`: AWS 액세스 키
- `AWS_SECRET_KEY`: AWS 시크릿 키

### 이메일 관련
- `MAIL_PASSWORD`: 이메일 비밀번호 (기존 application.yml에서 사용하던 값)

## 환경 변수 설정 방법

1. AWS Elastic Beanstalk 콘솔에 로그인합니다.
2. 해당 애플리케이션과 환경을 선택합니다.
3. 왼쪽 사이드바에서 "Configuration"을 클릭합니다.
4. "Software" 카테고리에서 "Edit"을 클릭합니다.
5. "Environment properties" 섹션에서 위의 환경 변수들을 추가합니다.
6. "Apply"를 클릭하여 변경사항을 저장합니다.

## 주의사항

- 환경 변수에 민감한 정보가 포함되어 있으므로, 해당 환경에 대한 접근 권한을 적절히 관리해주세요.
- 배포 전에 모든 환경 변수가 올바르게 설정되었는지 확인해주세요.
- AWS RDS와 ElastiCache(Redis)는 별도로 설정해야 합니다.