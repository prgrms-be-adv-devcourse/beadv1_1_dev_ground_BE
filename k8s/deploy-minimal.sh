#!/bin/bash

# K8s 최소 배포 스크립트 (Nginx, Eureka, Gateway, MySQL, Commerce, Payments)
# 사용법: bash ./k8s/deploy-minimal.sh

set -e  # 오류 발생 시 스크립트 중단

echo "========================================="
echo "K8s 최소 배포 시작"
echo "========================================="

# 1. ConfigMap 및 Secret 배포
echo ""
echo "[1/5] ConfigMap 및 Secret 배포 중..."
kubectl apply -f ./k8s/exec/config/db/mysql-init-configmap.yml
kubectl apply -f ./k8s/exec/config/svc/secret.yml
kubectl apply -f ./k8s/exec/config/lb/nginx-configmap.yml

echo "✓ ConfigMap 및 Secret 배포 완료"

# 2. Database (MySQL) 배포
echo ""
echo "[2/5] MySQL Database 배포 중..."
kubectl apply -f ./k8s/exec/outer/db/mysqldb-deployment.yml
kubectl apply -f ./k8s/exec/outer/db/mysqldb-service.yml

echo "✓ MySQL 배포 완료"
echo "  MySQL이 준비될 때까지 30초 대기 중..."
sleep 30

# 3. Infrastructure (Eureka, Gateway) 배포
echo ""
echo "[3/5] Infrastructure (Eureka, Gateway) 배포 중..."
kubectl apply -f ./k8s/exec/outer/infra/eureka-deployment.yml
kubectl apply -f ./k8s/exec/outer/infra/eureka-service.yml

echo "✓ Eureka 배포 완료"
echo "  Eureka가 준비될 때까지 30초 대기 중..."
sleep 30

kubectl apply -f ./k8s/exec/outer/infra/gateway-deployment.yml
kubectl apply -f ./k8s/exec/outer/infra/gateway-service.yml

echo "✓ Gateway 배포 완료"

# 4. Nginx LoadBalancer 배포
echo ""
echo "[4/5] Nginx LoadBalancer 배포 중..."
kubectl apply -f ./k8s/exec/config/lb/nginx-deployment.yml
kubectl apply -f ./k8s/exec/config/lb/nginx-service.yml

echo "✓ Nginx 배포 완료"

# 5. Application Services (Commerce, Payments) 배포
echo ""
echo "[5/5] Application Services (Commerce, Payments) 배포 중..."
kubectl apply -f ./k8s/exec/outer/service/commerce-deployment.yml
kubectl apply -f ./k8s/exec/outer/service/commerce-service.yml
kubectl apply -f ./k8s/exec/outer/service/payments-deployment.yml
kubectl apply -f ./k8s/exec/outer/service/payments-service.yml

echo "✓ Application Services 배포 완료"

echo ""
echo "========================================="
echo "✓ 최소 배포가 완료되었습니다!"
echo "========================================="
echo ""
echo "배포된 서비스:"
echo "  - MySQL Database"
echo "  - Eureka (Service Discovery)"
echo "  - Gateway (API Gateway)"
echo "  - Nginx (LoadBalancer)"
echo "  - Commerce Service"
echo "  - Payments Service"
echo ""
echo "배포 상태 확인:"
echo "  kubectl get pods"
echo "  kubectl get services"
echo ""