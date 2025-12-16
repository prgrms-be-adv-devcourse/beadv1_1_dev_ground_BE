#!/bin/bash

# Nginx + SSL 배포 전용 스크립트
# 사용법: EC2에서 실행 - bash ./k8s/deploy-nginx-with-ssl.sh

set -e

echo "========================================="
echo "Nginx + SSL 배포 시작"
echo "========================================="

export KUBECONFIG=/etc/rancher/k3s/k3s.yaml

# 1단계: certbot 설치
echo ""
echo "========================================="
echo "[1단계] certbot 설치 중..."
echo "========================================="

sudo apt update
sudo apt install certbot -y

echo "✓ certbot 설치 완료"

# 2단계: SSL 인증서 발급 (DRY-RUN 테스트)
echo ""
echo "========================================="
echo "[2단계] SSL 인증서 발급 테스트 (DRY-RUN)..."
echo "========================================="
echo ""
echo "주의: dbay.site가 현재 서버 IP를 가리켜야 합니다!"
echo "Let's Encrypt Rate Limit: 주당 5회 실패 제한"
echo ""
echo "잠시 대기 중..."
sleep 3

# DRY-RUN 테스트 실행
echo ""
echo "[테스트 모드] --dry-run으로 인증서 발급 테스트 중..."
sudo certbot certonly --standalone -d dbay.site -d www.dbay.site --dry-run --non-interactive --agree-tos --email dbay.auth@gmail.com

echo ""
echo "✓ DRY-RUN 테스트 성공!"
echo ""
read -p "실제 인증서를 발급받으시겠습니까? (y/N): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo ""
    echo "실제 SSL 인증서 발급 중..."
    sudo certbot certonly --standalone -d dbay.site -d www.dbay.site --non-interactive --agree-tos --email dbay.auth@gmail.com
    echo "✓ 인증서 발급 완료"
else
    echo ""
    echo "⚠️  실제 인증서 발급을 건너뜁니다."
    echo "테스트만 완료되었습니다. 나중에 다시 실행하세요."
    exit 0
fi

# 3단계: k8s Secret 생성
echo ""
echo "========================================="
echo "[3단계] k8s Secret 생성 중..."
echo "========================================="

sudo -E kubectl create secret tls dbay-tls-secret \
  --cert=/etc/letsencrypt/live/dbay.site/fullchain.pem \
  --key=/etc/letsencrypt/live/dbay.site/privkey.pem \
  --dry-run=client -o yaml | sudo -E kubectl apply -f -

echo "✓ Secret 생성 완료"

# 4단계: Nginx ConfigMap 배포
echo ""
echo "========================================="
echo "[4단계] Nginx ConfigMap 배포 중..."
echo "========================================="

sudo -E kubectl apply -f ./k8s/exec/config/lb/nginx-configmap.yml

echo "✓ ConfigMap 배포 완료"

# 5단계: Nginx Service 배포
echo ""
echo "========================================="
echo "[5단계] Nginx Service 배포 중..."
echo "========================================="

sudo -E kubectl apply -f ./k8s/exec/config/lb/nginx-service.yml

echo "✓ Service 배포 완료"

# 6단계: Nginx Deployment 배포 (SSL 적용)
echo ""
echo "========================================="
echo "[6단계] Nginx Deployment 배포 (SSL 적용)..."
echo "========================================="

sudo -E kubectl apply -f ./k8s/exec/config/lb/nginx-deployment.yml

echo "✓ Nginx Deployment 배포 완료"

# 배포 완료
echo ""
echo "========================================="
echo "✓ Nginx + SSL 배포 완료!"
echo "========================================="
echo ""
echo "배포된 리소스:"
echo "  - Nginx ConfigMap (nginx-config)"
echo "  - Nginx Service (nginx-proxy-manager)"
echo "  - Nginx Deployment (nginx-proxy-manager)"
echo "  - TLS Secret (dbay-tls-secret)"
echo ""
echo "SSL 인증서:"
echo "  - 도메인: dbay.site, www.dbay.site"
echo "  - 유효기간: 90일 (2주 프로젝트이므로 자동 갱신 없음)"
echo ""
echo "배포 상태 확인:"
echo "  sudo kubectl get pods -l app=nginx-proxy-manager"
echo "  sudo kubectl get service nginx-proxy-manager"
echo "  sudo kubectl get secret dbay-tls-secret"
echo ""
echo "접속 테스트:"
echo "  https://dbay.site"
echo ""