#!/bin/bash

# K8s 전체 삭제 스크립트
# 사용법: bash ./k8s/deploy-cleanup.sh

set -e  # 오류 발생 시 스크립트 중단

echo "========================================="
echo "K8s 리소스 전체 삭제 시작"
echo "========================================="

# 1. Application Services 삭제
echo ""
echo "[1/7] Application Services 삭제 중..."
kubectl delete -f ./k8s/exec/outer/service/user-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/service/user-service.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/service/product-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/service/product-service.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/service/commerce-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/service/commerce-service.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/service/payments-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/service/payments-service.yml --ignore-not-found=true

echo "✓ Application Services 삭제 완료"

# 2. Infra 및 LoadBalancer 삭제
echo ""
echo "[2/7] Infrastructure 및 LoadBalancer 삭제 중..."
kubectl delete -f ./k8s/exec/outer/infra/eureka-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/infra/eureka-service.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/infra/gateway-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/infra/gateway-service.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/lb/nginx-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/lb/nginx-service.yml --ignore-not-found=true

echo "✓ Infrastructure 삭제 완료"

# 3. Monitoring (Prometheus, Grafana) 삭제
echo ""
echo "[3/7] Monitoring Stack 삭제 중..."
kubectl delete -f ./k8s/exec/outer/audit/prometheus-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/audit/prometheus-service.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/audit/grafana-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/audit/grafana-service.yml --ignore-not-found=true

echo "✓ Monitoring Stack 삭제 완료"

# 4. ELK Stack 삭제
echo ""
echo "[4/7] ELK Stack 삭제 중..."
kubectl delete -f ./k8s/exec/outer/elk/elasticsearch-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/elk/elasticsearch-service.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/elk/kibana-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/elk/kibana-service.yml --ignore-not-found=true

echo "✓ ELK Stack 삭제 완료"

# 5. Message Queue (Kafka) 삭제
echo ""
echo "[5/7] Message Queue (Kafka) 삭제 중..."
kubectl delete -f ./k8s/exec/outer/mq/kafka-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/mq/kafka-service.yml --ignore-not-found=true

echo "✓ Kafka 삭제 완료"

# 6. Database 삭제
echo ""
echo "[6/7] Database 삭제 중..."
kubectl delete -f ./k8s/exec/outer/db/userdb-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/db/userdb-service.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/db/mysqldb-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/outer/db/mysqldb-service.yml --ignore-not-found=true

echo "✓ Database 삭제 완료"

# 7. ConfigMap 및 Secret 삭제
echo ""
echo "[7/7] ConfigMap 및 Secret 삭제 중..."
kubectl delete -f ./k8s/exec/config/db/mariadb-init-configmap.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/config/db/mysql-init-configmap.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/config/svc/secret.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/config/mq/kafka-configmap.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/config/elk/el-configmap.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/config/elk/kb-configmap.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/config/elk/logstash-configmap.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/config/audit/prometheus-configmap.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/config/lb/nginx-deployment.yml --ignore-not-found=true
kubectl delete -f ./k8s/exec/config/lb/nginx-service.yml --ignore-not-found=true

echo "✓ ConfigMap 및 Secret 삭제 완료"

echo ""
echo "========================================="
echo "✓ 모든 리소스가 삭제되었습니다!"
echo "========================================="
echo ""
echo "남은 리소스 확인:"
echo "  kubectl get all --all-namespaces"
echo ""

# 기존 deployment 삭제
sudo kubectl apply -f ./k8s/exec/outer/db/mysqldb-pvc.yml
sudo kubectl apply -f ./k8s/exec/outer/db/mariadb-pvc.yml
sudo kubectl apply -f ./k8s/exec/outer/elk/elasticsearch-pvc.yml

# Deployment 재생성
sudo kubectl apply -f ./k8s/exec/outer/db/mysqldb-deployment.yml
sudo kubectl apply -f ./k8s/exec/outer/db/userdb-deployment.yml
sudo kubectl apply -f ./k8s/exec/outer/elk/elasticsearch-deployment.yml