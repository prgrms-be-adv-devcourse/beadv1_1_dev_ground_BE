#!/bin/bash

# Nginx Proxy Manager 배포 스크립트
# 사용법: EC2에서 실행 - bash ./k8s/deploy-nginx-proxy-manager.sh

set -e

echo "========================================="
echo "Nginx Proxy Manager 배포 시작"
echo "========================================="

export KUBECONFIG=/etc/rancher/k3s/k3s.yaml

# 1단계: PersistentVolumeClaim 생성
echo ""
echo "========================================="
echo "[1단계] PersistentVolumeClaim 생성 중..."
echo "========================================="

sudo -E kubectl apply -f ./exec/config/lb/nginx-pvc.yml

echo "✓ PVC 생성 완료"

# 2단계: Nginx Service 배포
echo ""
echo "========================================="
echo "[2단계] Nginx Service 배포 중..."
echo "========================================="

sudo -E kubectl apply -f ./exec/config/lb/nginx-service.yml

echo "✓ Service 배포 완료"

# 3단계: Nginx Proxy Manager Deployment 배포
echo ""
echo "========================================="
echo "[3단계] Nginx Proxy Manager Deployment 배포 중..."
echo "========================================="

sudo -E kubectl apply -f ./exec/config/lb/nginx-deployment.yml

echo "✓ Deployment 배포 완료"

# 4단계: 배포 상태 확인
echo ""
echo "========================================="
echo "[4단계] 배포 상태 확인 중..."
echo "========================================="

echo ""
echo "Pod 대기 중 (최대 120초)..."
sudo -E kubectl wait --for=condition=ready pod -l app=nginx-proxy-manager --timeout=120s || true

echo ""
echo "--- Pod 상태 ---"
sudo -E kubectl get pods -l app=nginx-proxy-manager

echo ""
echo "--- Service 상태 ---"
sudo -E kubectl get service nginx-proxy-manager

echo ""
echo "--- PVC 상태 ---"
sudo -E kubectl get pvc

echo ""
echo "--- External-IP 확인 ---"
EXTERNAL_IP=$(sudo -E kubectl get service nginx-proxy-manager -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
if [ -z "$EXTERNAL_IP" ]; then
    echo "⚠️  External-IP가 아직 할당되지 않았습니다. 잠시 후 다시 확인하세요."
    echo "확인 명령어: sudo kubectl get service nginx-proxy-manager"
else
    echo "✓ External-IP: $EXTERNAL_IP"
fi

# 배포 완료
echo ""
echo "========================================="
echo "✓ Nginx Proxy Manager 배포 완료!"
echo "========================================="
echo ""
echo "배포된 리소스:"
echo "  - Nginx Proxy Manager Deployment"
echo "  - Nginx Service (LoadBalancer)"
echo "  - PersistentVolumeClaim (data, letsencrypt)"
echo ""
echo "관리자 웹 UI 접속:"
echo "  http://dbay.site:81"
echo "  또는 http://43.201.80.95:81"
echo ""
echo "기본 로그인 정보:"
echo "  Email: admin@example.com"
echo "  Password: changeme"
echo "  ⚠️  최초 로그인 후 반드시 비밀번호를 변경하세요!"
echo ""
echo "SSL 인증서 발급 방법 (웹 UI에서):"
echo "  1. 81번 포트로 접속"
echo "  2. SSL Certificates 메뉴 클릭"
echo "  3. Add SSL Certificate 클릭"
echo "  4. Let's Encrypt 선택"
echo "  5. 도메인 입력: dbay.site, www.dbay.site"
echo "  6. 이메일 입력: dbay.auth@gmail.com"
echo "  7. Agree to terms 체크"
echo "  8. Save 클릭"
echo ""
echo "프록시 호스트 설정 방법 (웹 UI에서):"
echo "  1. Proxy Hosts 메뉴 클릭"
echo "  2. Add Proxy Host 클릭"
echo "  3. Domain Names: dbay.site, www.dbay.site"
echo "  4. Scheme: http"
echo "  5. Forward Hostname/IP: gateway"
echo "  6. Forward Port: 8000"
echo "  7. SSL 탭에서 발급받은 인증서 선택"
echo "  8. Force SSL 체크"
echo "  9. Save 클릭"
echo ""
echo "배포 상태 확인:"
echo "  sudo kubectl get pods -l app=nginx-proxy-manager"
echo "  sudo kubectl get service nginx-proxy-manager"
echo "  sudo kubectl logs <pod-name>"
echo ""
echo "접속 테스트:"
echo "  http://dbay.site:81 (관리자 UI)"
echo "  https://dbay.site (설정 후)"
echo ""