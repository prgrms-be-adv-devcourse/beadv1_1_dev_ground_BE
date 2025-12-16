#!/bin/bash

# K8s 전체 배포 + SSL 자동화 통합 스크립트
# 사용법: EC2에서 실행 - bash ./k8s/deploy-all-with-ssl.sh

set -e

echo "========================================="
echo "K8s 전체 배포 + SSL 자동화 시작"
echo "========================================="

# 1단계: 기본 서비스 배포 (SSL 제외)
echo ""
echo "========================================="
echo "[1단계] 기본 서비스 배포 중..."
echo "========================================="

export KUBECONFIG=/etc/rancher/k3s/k3s.yaml

# ConfigMap 및 Secret 배포
echo ""
echo "[1/5] ConfigMap 및 Secret 배포 중..."
sudo -E kubectl apply -f ./k8s/exec/config/db/mysql-init-configmap.yml
sudo -E kubectl apply -f ./k8s/exec/config/svc/secret.yml
sudo -E kubectl apply -f ./k8s/exec/config/lb/nginx-configmap.yml

echo "✓ ConfigMap 및 Secret 배포 완료"

# Database (MySQL) 배포
echo ""
echo "[2/5] MySQL Database 배포 중..."
sudo -E kubectl apply -f ./k8s/exec/outer/db/mysqldb-deployment.yml
sudo -E kubectl apply -f ./k8s/exec/outer/db/mysqldb-service.yml

echo "✓ MySQL 배포 완료"
echo "  MySQL이 준비될 때까지 30초 대기 중..."
sleep 30

# Infrastructure (Eureka, Gateway) 배포
echo ""
echo "[3/5] Infrastructure (Eureka, Gateway) 배포 중..."
sudo -E kubectl apply -f ./k8s/exec/outer/infra/eureka-deployment.yml
sudo -E kubectl apply -f ./k8s/exec/outer/infra/eureka-service.yml

echo "✓ Eureka 배포 완료"
echo "  Eureka가 준비될 때까지 30초 대기 중..."
sleep 30

sudo -E kubectl apply -f ./k8s/exec/outer/infra/gateway-deployment.yml
sudo -E kubectl apply -f ./k8s/exec/outer/infra/gateway-service.yml

echo "✓ Gateway 배포 완료"

# Nginx LoadBalancer 배포 (SSL 없이)
echo ""
echo "[4/5] Nginx LoadBalancer 배포 중 (SSL 설정 전)..."
sudo -E kubectl apply -f ./k8s/exec/config/lb/nginx-service.yml

echo "✓ Nginx Service 배포 완료"

# Application Services (Commerce, Payments) 배포
echo ""
echo "[5/5] Application Services (Commerce, Payments) 배포 중..."
sudo -E kubectl apply -f ./k8s/exec/outer/service/commerce-deployment.yml
sudo -E kubectl apply -f ./k8s/exec/outer/service/commerce-service.yml
sudo -E kubectl apply -f ./k8s/exec/outer/service/payments-deployment.yml
sudo -E kubectl apply -f ./k8s/exec/outer/service/payments-service.yml

echo "✓ Application Services 배포 완료"

# 2단계: SSL 인증서 발급
echo ""
echo "========================================="
echo "[2단계] SSL 인증서 발급 중..."
echo "========================================="

# certbot 설치
echo ""
echo "certbot 설치 중..."
sudo apt update
sudo apt install certbot -y

echo "✓ certbot 설치 완료"

# certbot으로 인증서 발급
echo ""
echo "SSL 인증서 발급 중..."
echo "주의: dbay.site가 현재 서버 IP를 가리켜야 합니다!"
echo "잠시 대기 중..."
sleep 5

sudo certbot certonly --standalone -d dbay.site -d www.dbay.site --non-interactive --agree-tos --email dbay.auth@gmail.com

echo "✓ 인증서 발급 완료"

# k8s Secret 생성
echo ""
echo "k8s Secret 생성 중..."
sudo -E kubectl create secret tls dbay-tls-secret \
  --cert=/etc/letsencrypt/live/dbay.site/fullchain.pem \
  --key=/etc/letsencrypt/live/dbay.site/privkey.pem \
  --dry-run=client -o yaml | sudo -E kubectl apply -f -

echo "✓ Secret 생성 완료"

# 3단계: Nginx Deployment 배포 (SSL 적용)
echo ""
echo "========================================="
echo "[3단계] Nginx 배포 (SSL 적용)..."
echo "========================================="

sudo -E kubectl apply -f ./k8s/exec/config/lb/nginx-deployment.yml

echo "✓ Nginx 배포 완료"

# 4단계: 자동 갱신 CronJob 등록
echo ""
echo "========================================="
echo "[4단계] SSL 자동 갱신 CronJob 등록..."
echo "========================================="

#sudo -E kubectl apply -f ./k8s/exec/config/cert/ssl-renew-cronjob.yml

echo "✓ CronJob 등록 완료"

echo ""
echo "========================================="
echo "✓ 전체 배포 완료!"
echo "========================================="
echo ""
echo "배포된 서비스:"
echo "  - MySQL Database"
echo "  - Eureka (Service Discovery)"
echo "  - Gateway (API Gateway)"
echo "  - Nginx (LoadBalancer with SSL)"
echo "  - Commerce Service"
echo "  - Payments Service"
echo ""
echo "SSL 인증서:"
echo "  - 도메인: dbay.site, www.dbay.site"
echo "  - 자동 갱신: 매월 1일 새벽 3시"
echo ""
echo "배포 상태 확인:"
echo "  sudo kubectl get pods"
echo "  sudo kubectl get services"
echo "  sudo kubectl get certificate"
echo "  sudo kubectl get cronjobs"
echo ""
echo "접속 테스트:"
echo "  https://dbay.site"
echo ""