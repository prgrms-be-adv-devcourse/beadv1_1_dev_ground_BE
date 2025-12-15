#!/bin/bash

# K8s 초기 배포 스크립트
# 사용법: ./k8s/deploy-init.sh

set -e  # 오류 발생 시 스크립트 중단

echo "========================================="
echo "K8s 초기 배포 시작"
echo "========================================="

# 1. ConfigMap 및 Secret 배포
echo ""
echo "[1/6] ConfigMap 및 Secret 배포 중..."
kubectl apply -f ./k8s/exec/config/db/mariadb-init-configmap.yml
kubectl apply -f ./k8s/exec/config/db/mysql-init-configmap.yml
kubectl apply -f ./k8s/exec/config/svc/secret.yml
kubectl apply -f ./k8s/exec/config/mq/kafka-configmap.yml
kubectl apply -f ./k8s/exec/config/elk/el-configmap.yml
kubectl apply -f ./k8s/exec/config/elk/kb-configmap.yml
kubectl apply -f ./k8s/exec/config/elk/logstash-configmap.yml
kubectl apply -f ./k8s/exec/config/audit/prometheus-configmap.yml
kubectl apply -f ./k8s/exec/config/lb/nginx-configmap.yml
kubectl apply -f ./k8s/exec/config/lb/nginx-deployment.yml
kubectl apply -f ./k8s/exec/config/lb/nginx-service.yml

echo "✓ ConfigMap 및 Secret 배포 완료"

# 2. Database 배포
echo ""
echo "[2/6] Database 배포 중..."
kubectl apply -f ./k8s/exec/outer/db/userdb-deployment.yml
kubectl apply -f ./k8s/exec/outer/db/userdb-service.yml
kubectl apply -f ./k8s/exec/outer/db/mysqldb-deployment.yml
kubectl apply -f ./k8s/exec/outer/db/mysqldb-service.yml

echo "✓ Database 배포 완료"
echo "  Database가 준비될 때까지 30초 대기 중..."
sleep 30

# 3. Message Queue (Kafka) 배포
echo ""
echo "[3/6] Message Queue (Kafka) 배포 중..."
kubectl apply -f ./k8s/exec/outer/mq/kafka-deployment.yml
kubectl apply -f ./k8s/exec/outer/mq/kafka-service.yml

echo "✓ Kafka 배포 완료"
echo "  Kafka가 준비될 때까지 20초 대기 중..."
sleep 20

# 4. ELK Stack 배포
echo ""
echo "[4/6] ELK Stack 배포 중..."
kubectl apply -f ./k8s/exec/outer/elk/elasticsearch-deployment.yml
kubectl apply -f ./k8s/exec/outer/elk/elasticsearch-service.yml
kubectl apply -f ./k8s/exec/outer/elk/kibana-deployment.yml
kubectl apply -f ./k8s/exec/outer/elk/kibana-service.yml

echo "✓ ELK Stack 배포 완료"

# 5. Monitoring (Prometheus, Grafana) 배포
echo ""
echo "[5/6] Monitoring Stack 배포 중..."
kubectl apply -f ./k8s/exec/outer/audit/prometheus-deployment.yml
kubectl apply -f ./k8s/exec/outer/audit/prometheus-service.yml
kubectl apply -f ./k8s/exec/outer/audit/grafana-deployment.yml
kubectl apply -f ./k8s/exec/outer/audit/grafana-service.yml

echo "✓ Monitoring Stack 배포 완료"

# 6. Infra 및 LoadBalancer 배포
echo ""
echo "[6/6] Infrastructure 및 LoadBalancer 배포 중..."
kubectl apply -f ./k8s/exec/outer/infra/eureka-deployment.yml
kubectl apply -f ./k8s/exec/outer/infra/eureka-service.yml
kubectl apply -f ./k8s/exec/outer/infra/gateway-deployment.yml
kubectl apply -f ./k8s/exec/outer/infra/gateway-service.yml

echo "✓ Infrastructure 배포 완료"
echo "  Eureka가 준비될 때까지 30초 대기 중..."
sleep 30

# 7. Application Services 배포
echo ""
echo "[7/7] Application Services 배포 중..."
kubectl apply -f ./k8s/exec/outer/service/user-deployment.yml
kubectl apply -f ./k8s/exec/outer/service/user-service.yml
kubectl apply -f ./k8s/exec/outer/service/product-deployment.yml
kubectl apply -f ./k8s/exec/outer/service/product-service.yml
kubectl apply -f ./k8s/exec/outer/service/commerce-deployment.yml
kubectl apply -f ./k8s/exec/outer/service/commerce-service.yml
kubectl apply -f ./k8s/exec/outer/service/payments-deployment.yml
kubectl apply -f ./k8s/exec/outer/service/payments-service.yml

echo "✓ Application Services 배포 완료"

echo ""
echo "========================================="
echo "✓ 모든 배포가 완료되었습니다!"
echo "========================================="
echo ""
echo "배포 상태 확인:"
echo "  kubectl get pods --all-namespaces"
echo "  kubectl get services --all-namespaces"
echo ""
