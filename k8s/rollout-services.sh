#!/bin/bash

# K8s 서비스 롤아웃 스크립트
# 사용법: EC2에서 실행 - bash ./k8s/rollout-services.sh

set -e

echo "========================================="
echo "서비스 롤아웃 시작"
echo "========================================="

# PVC 생성 (먼저)
echo ""
echo "========================================="
echo "PVC 생성 중..."
echo "========================================="
sudo kubectl apply -f ./k8s/exec/outer/db/mariadb-pvc.yml
sudo kubectl apply -f ./k8s/exec/outer/db/mysqldb-pvc.yml
sudo kubectl apply -f ./k8s/exec/outer/elk/elasticsearch-pvc.yml
echo "✓ PVC 생성 완료"
echo "  PVC가 바인딩될 때까지 5초 대기 중..."
sleep 5

# Infrastructure 서비스 롤아웃
echo ""
echo "========================================="
echo "Infrastructure 서비스 롤아웃"
echo "========================================="

# 1. MariaDB
echo ""
echo "[1/7] MariaDB 롤아웃 중..."
sudo kubectl rollout restart deployment mariadb
sudo kubectl rollout status deployment mariadb
echo "✓ MariaDB 롤아웃 완료"

# 2. MySQL
echo ""
echo "[2/7] MySQL 롤아웃 중..."
sudo kubectl rollout restart deployment mysql-image
sudo kubectl rollout status deployment mysql-image
echo "✓ MySQL 롤아웃 완료"

# 3. Redis
echo ""
echo "[3/7] Redis 롤아웃 중..."
sudo kubectl rollout restart deployment redis
sudo kubectl rollout status deployment redis
echo "✓ Redis 롤아웃 완료"

# 4. Elasticsearch
echo ""
echo "[4/7] Elasticsearch 롤아웃 중..."
sudo kubectl rollout restart deployment elasticsearch
sudo kubectl rollout status deployment elasticsearch
echo "✓ Elasticsearch 롤아웃 완료"

echo ""
echo "Infrastructure 준비 완료. 10초 대기 중..."
sleep 10

# Application 서비스 롤아웃
echo ""
echo "========================================="
echo "Application 서비스 롤아웃"
echo "========================================="

# 5. User Service
echo ""
echo "[5/7] User Service 롤아웃 중..."
sudo kubectl rollout restart deployment user-service
sudo kubectl rollout status deployment user-service
echo "✓ User Service 롤아웃 완료"

# 6. Product Service
echo ""
echo "[6/7] Product Service 롤아웃 중..."
sudo kubectl rollout restart deployment product-service
sudo kubectl rollout status deployment product-service
echo "✓ Product Service 롤아웃 완료"

# 7. Payments Service
echo ""
echo "[7/7] Payments Service 롤아웃 중..."
sudo kubectl rollout restart deployment payments-service
sudo kubectl rollout status deployment payments-service
echo "✓ Payments Service 롤아웃 완료"

# 8. Commerce Service
echo ""
echo "[8/7] Commerce Service 롤아웃 중..."
sudo kubectl rollout restart deployment commerce-service
sudo kubectl rollout status deployment commerce-service
echo "✓ Commerce Service 롤아웃 완료"

echo ""
echo "========================================="
echo "✓ 모든 서비스 롤아웃 완료!"
echo "========================================="
echo ""
echo "Pod 상태 확인:"
sudo kubectl get pods
echo ""
echo "PVC 상태 확인:"
sudo kubectl get pvc
echo ""
echo "Eureka 대시보드에서 서비스 등록 확인:"
echo "  https://dbay.site:8761"
echo ""