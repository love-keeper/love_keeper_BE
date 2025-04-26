#!/bin/bash

# 색상 설정
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== LoveKeeper ELB 배포 패키지 생성 스크립트 ===${NC}"

# 1. 프로젝트 빌드
echo -e "${GREEN}프로젝트 빌드 중...${NC}"
./gradlew clean bootJar

if [ $? -ne 0 ]; then
    echo -e "${RED}빌드 실패! 오류를 확인해주세요.${NC}"
    exit 1
fi

# 2. 배포 패키지 디렉토리 생성
echo -e "${GREEN}배포 패키지 디렉토리 생성 중...${NC}"
mkdir -p build/dist

# 3. JAR 파일 복사
echo -e "${GREEN}JAR 파일 복사 중...${NC}"
cp build/libs/*.jar app.jar

# 4. ZIP 배포 패키지 생성
echo -e "${GREEN}ZIP 배포 패키지 생성 중...${NC}"
zip -r "build/dist/lovekeeper.zip" Procfile Dockerfile .ebextensions app.jar

echo -e "${BLUE}배포 패키지가 성공적으로 생성되었습니다!${NC}"
echo -e "${GREEN}패키지 위치: build/dist/lovekeeper.zip${NC}"
echo -e "${GREEN}이 파일을 AWS Elastic Beanstalk 콘솔에서 업로드하세요.${NC}"
