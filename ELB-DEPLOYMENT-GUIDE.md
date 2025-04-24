# LoveKeeper 프로젝트 AWS Elastic Beanstalk 배포 가이드

이 문서는 LoveKeeper 프로젝트를 AWS Elastic Beanstalk(ELB)에 배포하기 위한 단계별 가이드입니다.

## 준비 사항

1. AWS 계정
2. AWS CLI 및 EB CLI 설치
3. 적절한 권한을 가진 IAM 사용자
4. MySQL RDS 인스턴스 (또는 다른 외부 데이터베이스)
5. Redis ElastiCache (또는 다른 외부 Redis 인스턴스)

## 배포 단계

### 1. EB CLI 설치 및 초기화

**EB CLI 설치:**

```bash
pip install awsebcli
```

**EB CLI 초기화:**

```bash
cd /path/to/lovekeeper
eb init
```

초기화 과정에서 다음 정보를 입력해야 합니다:

- AWS 리전 선택
- 애플리케이션 이름 (새로 생성하거나 기존 선택)
- 플랫폼 선택 (Docker)
- SSH 접속 설정 여부

### 2. 애플리케이션 빌드

배포 전에 애플리케이션을 빌드하고 배포 패키지를 생성합니다:

```bash
./gradlew clean bootJar ebDeploy
```
   
이 명령어는 Spring Boot JAR 파일을 빌드하고, ELB 배포에 필요한 ZIP 패키지를 `build/dist/` 디렉토리에 생성합니다.

### 3. 애플리케이션 배포

다음 명령어로 애플리케이션을 배포합니다:

```bash
eb create lovekeeper-env --platform docker
```

또는 기존 환경에 배포하려면:

```bash
eb deploy
```

### 4. 환경 변수 설정

배포 후, AWS 콘솔에서 환경 변수를 설정해야 합니다. 자세한 내용은 `eb-environment-variables.md` 파일을 참조하세요.

### 5. 배포 상태 확인

배포 상태를 확인하려면:

```bash
eb status
```

로그를 확인하려면:

```bash
eb logs
```

## 배포 스크립트 사용 (선택 사항)

프로젝트 루트에 있는 `deploy-to-eb.sh` 스크립트를 사용하여 배포 과정을 자동화할 수 있습니다:

```bash
chmod +x deploy-to-eb.sh
./deploy-to-eb.sh
```

## 주요 설정 파일

- **Dockerfile**: 애플리케이션 Docker 이미지를 정의합니다.
- **.ebextensions/**: ELB 환경 구성 파일이 포함된 디렉토리입니다.
- **Procfile**: ELB에서 애플리케이션을 시작하는 명령어를 정의합니다.
- **application-elb.yml**: ELB 환경에 맞는 Spring Boot 설정 파일입니다.

## 문제 해결

1. **환경 생성 실패**:
    - ELB 콘솔에서 이벤트 로그 확인
    - `eb logs` 명령어로 로그 확인

2. **애플리케이션 시작 실패**:
    - Dockerfile 및 Procfile 확인
    - 환경 변수가 올바르게 설정되었는지 확인

3. **데이터베이스 연결 오류**:
    - RDS 보안 그룹 설정 확인
    - 환경 변수에 올바른 RDS 정보가 포함되어 있는지 확인

## 추가 리소스

- [AWS Elastic Beanstalk 문서](https://docs.aws.amazon.com/elastic-beanstalk/latest/dg/Welcome.html)
- [Spring Boot on AWS Elastic Beanstalk](https://docs.aws.amazon.com/elastic-beanstalk/latest/dg/java-se-platform.html)
- [Docker on AWS Elastic Beanstalk](https://docs.aws.amazon.com/elastic-beanstalk/latest/dg/create_deploy_docker.html)