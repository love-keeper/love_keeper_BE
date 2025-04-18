# 💖 Love Keeper - 커플 연결 및 관계 관리 플랫폼

<div align="center">
  <img src="https://img.shields.io/badge/spring_boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white" alt="AWS" />
  <img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white" alt="GitHub Actions" />
</div>

## 📱 프로젝트 소개

**Love Keeper**는 커플을 위한 연결 및 관계 관리 플랫폼입니다. 커플 간의 소통과 추억 공유를 돕고, 관계를 더욱 특별하게 만들어주는 다양한 기능을 제공합니다.

<div align="center">
  <img src="https://via.placeholder.com/800x400?text=Love+Keeper+Screenshot" alt="Love Keeper Screenshot" width="80%" />
</div>

## ✨ 주요 기능

- **커플 연결**: 초대 코드를 통한 간편한 커플 연결
- **편지 교환**: 서로에게 마음을 전하는 디지털 편지 기능
- **약속 관리**: 함께하는 약속을 기록하고 알림 받기
- **캘린더**: 커플 전용 캘린더로 중요한 날짜 관리
- **소셜 로그인**: 다양한 소셜 플랫폼을 통한 간편 로그인 지원
- **푸시 알림**: Firebase Cloud Messaging을 통한 실시간 알림

## 🛠 기술 스택

### 백엔드
- **언어**: Java 17
- **프레임워크**: Spring Boot 3.4
- **보안**: Spring Security, JWT
- **데이터베이스**: MySQL 8.0
- **캐싱**: Redis
- **푸시 알림**: Firebase Cloud Messaging

### 인프라
- **배포**: AWS EC2, Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **웹 서버**: Nginx
- **SSL**: Let's Encrypt
- **도메인**: 가비아 DNS

## 🏗 아키텍처

```
클라이언트 <-> Nginx (HTTPS) <-> Spring Boot <-> MySQL/Redis
```

<div align="center">
  <img src="https://via.placeholder.com/800x500?text=Architecture+Diagram" alt="Architecture Diagram" width="80%" />
</div>

## 📂 프로젝트 구조

```
com.example.lovekeeper
├── domain
│   ├── auth            - 인증/회원가입 관련 기능
│   ├── calendar        - 캘린더 및 일정 관리 
│   ├── connectionhistory - 커플 연결 이력 관리
│   ├── couple          - 커플 관련 기능
│   ├── draft           - 편지 임시 저장 기능
│   ├── fcm             - Firebase 푸시 알림
│   ├── letter          - 편지 교환 기능
│   ├── member          - 회원 정보 관리
│   └── promise         - 약속 관리 기능
├── global
│   ├── common          - 공통 유틸리티
│   ├── config          - 애플리케이션 설정
│   ├── exception       - 전역 예외 처리
│   ├── infrastructure  - 인프라 연동 (S3, Redis 등)
│   ├── scheduler       - 배치 작업 스케줄러
│   └── security        - 보안 설정 및 JWT 관리
```

## 🚀 시작하기

### 선행 조건
- Java 17+
- Docker 및 Docker Compose
- MySQL 8.0
- Redis

### 로컬 개발 환경 설정

1. 저장소 클론
```bash
git clone https://github.com/your-username/lovekeeper.git
cd lovekeeper
```

2. 로컬 데이터베이스 설정
```bash
docker-compose up -d
```

3. 애플리케이션 실행
```bash
./gradlew bootRun
```

4. 애플리케이션 접속
```
http://localhost:8080
```

## 🌐 배포

애플리케이션은 GitHub Actions를 통한 CI/CD 파이프라인으로 자동 배포됩니다:
1. `develop` 브랜치에 코드가 푸시되면 빌드 시작
2. 테스트 및 빌드 진행
3. AWS EC2 인스턴스에 자동 배포
4. Nginx를 통한 HTTPS 요청 처리

## 📄 API 문서

API 문서는 Swagger를 통해 제공됩니다:
```
https://lovekeeper.site/swagger-ui/index.html
```

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 단위 테스트만 실행
./gradlew unitTest

# 통합 테스트만 실행
./gradlew integrationTest
```

## 👥 팀원

- 박동규 ([@dong99u](https://github.com/dong99u)) - 백엔드 개발, 인프라 설정

## 📝 라이센스

이 프로젝트는 MIT 라이센스를 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 확인하세요.

## 🔗 링크

- 서비스: [https://lovekeeper.site](https://lovekeeper.site)
- 이슈 트래커: [GitHub Issues](https://github.com/your-username/lovekeeper/issues)

---

<div align="center">
  <p>💖 Love Keeper - 소중한 관계를 더 특별하게</p>
</div>
