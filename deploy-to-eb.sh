#!/bin/bash

# LoveKeeper 프로젝트를 AWS Elastic Beanstalk에 배포하는 스크립트

# 색상 설정
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== LoveKeeper ELB 배포 스크립트 ===${NC}"

# 1. 프로젝트 빌드
echo -e "${GREEN}프로젝트 빌드 중...${NC}"
./gradlew clean bootJar

if [ $? -ne 0 ]; then
    echo -e "${RED}빌드 실패! 오류를 확인해주세요.${NC}"
    exit 1
fi

# 2. ELB 배포 패키지 생성
echo -e "${GREEN}ELB 배포 패키지 생성 중...${NC}"
./gradlew ebDeploy

if [ $? -ne 0 ]; then
    echo -e "${RED}배포 패키지 생성 실패! 오류를 확인해주세요.${NC}"
    exit 1
fi

# 3. EB CLI가 설치되어 있는지 확인
if ! command -v eb &> /dev/null; then
    echo -e "${RED}EB CLI가 설치되어 있지 않습니다. pip install awsebcli 명령어로 설치해주세요.${NC}"
    exit 1
fi

# 4. EB 초기화 확인
if [ ! -d .elasticbeanstalk ]; then
    echo -e "${BLUE}EB CLI 초기화가 필요합니다. 다음 명령어를 실행해주세요:${NC}"
    echo -e "${GREEN}eb init${NC}"
    exit 1
fi

# 5. AWS EB 배포
echo -e "${GREEN}AWS Elastic Beanstalk에 배포 중...${NC}"
eb deploy --staged

if [ $? -ne 0 ]; then
    echo -e "${RED}배포 실패! 오류를 확인해주세요.${NC}"
    exit 1
fi

echo -e "${BLUE}배포가 완료되었습니다!${NC}"
echo -e "${GREEN}애플리케이션 상태를 확인하려면 다음 명령어를 실행하세요: eb status${NC}"
echo -e "${GREEN}애플리케이션 로그를 확인하려면 다음 명령어를 실행하세요: eb logs${NC}"
