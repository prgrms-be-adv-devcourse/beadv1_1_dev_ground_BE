## ✏️ 프로젝트 개요
**DBAY**는 전자기기 중고거래 이용자를 위해, 신뢰할 수 있는 거래 환경과 **예치금 기반 안전 결제·정산, 실시간 검색·비딩을 제공하는 중고거래 플랫폼**입니다.</br>

## 📪 배포 링크
### [DBAY](https://dbay.site/)

## 📆 개발 기간
- **전체 개발 기간** : 2025.11.03 - 2025.12.19
- **주제 선정 및 기획** : 2025.11.03. ~ 2025.11.06
- **기능명세서 작성 및 역할 분배** : 2025.11.07 ~ 2025.11.10
- **기능 구현** : 2025.11.10 ~ 2025.12.18
- **마무리 및 발표준비** : 2025.12.18 ~ 2025.12.19

## 👨‍💻 개발인원 및 역할
| BE                                                                            | BE                                                                             | BE                                                                                 | BE                                                                                  | BE                                                                             |
|-------------------------------------------------------------------------------|--------------------------------------------------------------------------------|------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| <img src="https://avatars.githubusercontent.com/ljs981026" width=175 alt="이재석"> | <img src="https://avatars.githubusercontent.com/oneplast" width=175 alt="민경훈"> | <img src="https://avatars.githubusercontent.com/Baeyonghyeon" width=175 alt="배용현"> | <img src="https://avatars.githubusercontent.com/u/75302306?v=4" width=175 alt="진세현"> | <img src="https://avatars.githubusercontent.com/hanfihei" width=175 alt="한지혜"> |
| [이재석](https://github.com/ljs981026) **(팀장)**                                  | [민경훈](https://github.com/oneplast)                                             | [배용현](https://github.com/Baeyonghyeon)                                             | [진세현](https://github.com/niki8533)                                                                             | [한지혜](https://github.com/hanfihei)                                             |
| 장바구니, 주문 도메인 담당                                                               | 상품, 이미지 도메인 담당                                                                 | 예치금, 정산, 배포 담당                                                                     | 유저, 결제 도메인 담당                                                              | 채팅, 인프라 담당                                                                     |

## 🔊 주요 기능
#### 1. 회원 및 마이페이지
- 회원가입 / 로그인 / 로그아웃
- 소셜로그인 지원(kakao)
- 마이페이지에서 거래 상품 및 거래 상태 확인
- 사용자 판매 상품 조회
#### 2. 상품 관리 및 이미지
- 상품 등록, 수정, 삭제, 조회
- 상품 이미지 등록/추가/삭제 및 상품 이미지 기반 관리 흐름
#### 3. 검색 및 탐색
- **키워드 검색** : 자동완성, 연관 검색어 태그 제공
- **카테고리 기반 검색** : 상위 카테고리 선택 시 하위 카테고리 포함
- **가격 필터링, 정렬**: 최신순 / 가격순 / 업데이트 순 등
#### 4. 장바구니 및 주문
- 장바구니 추가/삭제/조회
- 장바구니 상품 주문(묶음 주문)
#### 5. 거래 및 결제
- 등록된 상품을 예치금으로 구매
- 상품 결제 시 예치금 차감
- 토스 페이(Toss) API로 예치금 충전/결제
#### 6. 정산
- 배달 완료 후 2주 경과한 거래 대상으로 정산 처리
#### 7. 채팅
- 판매자와 1:1 채팅
- 채팅방 목록 확인, 채팅방 선택
- 실시간 읽음 표시, 이전 메시지 저장
#### 8. 상품 추천
- 사용자 조회 이력 기반 추천(최근 7일 기준)
- 상품 상세에서 유사 상품 추천
- 이력이 부족하면 인기 상품 추천
#### 9. 아키텍처/운영
- Gateway/Eureka 기반 서비스 구성
- Elasticsearch, Kibana, Redis, Kafka 등 연동
- Kubernetes/Docker 기반 배포 흐름

## ⚒️ 개발 환경
### 📜 Project Docs
<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white"> <img src="https://img.shields.io/badge/ErdCloud-9370DB?style=for-the-badge&logo=ErdCloud&logoColor=white">

### 🖥 **System Architecture**
<img src="https://github.com/user-attachments/assets/b90c9a5a-1d07-4ea0-bab4-86c8fe4aa20b" alt="System Architecture">

### 🖥 **ERD Diagram**
<img src="https://github.com/user-attachments/assets/dd5bf20b-4540-4d22-a855-1b141387c27c" alt="ERD Diagram">

### 🛠 Tech Stack
💻 **Development**  
<img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white">  
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white"> <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">

⚡ **Real-Time**  
<img src="https://img.shields.io/badge/WebSocket-0088CC?style=for-the-badge&logo=websocket&logoColor=white"> <img src="https://img.shields.io/badge/STOMP-009ACE?style=for-the-badge&logo=apache&logoColor=white"> <img src="https://img.shields.io/badge/SSE-FF9900?style=for-the-badge&logo=eventbrite&logoColor=white">

🗄 **Database & ORM**  
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"> <img src="https://img.shields.io/badge/mongoDB-47A248?style=for-the-badge&logo=MongoDB&logoColor=white"> <img src="https://img.shields.io/badge/mariaDB-003545?style=for-the-badge&logo=mariaDB&logoColor=white"> <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white">  
<img src="https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=hibernate&logoColor=white"> 

📑 **Docs & API Testing**  
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white"> <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">

📊 **Observability & Monitoring**  
<img src="https://img.shields.io/badge/ElasticSearch-005571?style=for-the-badge&logo=ElasticSearch&logoColor=white"> <img src="https://img.shields.io/badge/Kibana-005571?style=for-the-badge&logo=Kibana&logoColor=white">

📨 **Messaging / Event Streaming**  
<img src="https://img.shields.io/badge/Apache Kafka-231F20?style=for-the-badge&logo=Apache Kafka&logoColor=white">

🔐 **Authentication & Security**  
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"> <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"> <img src="https://img.shields.io/badge/OAuth 2.0-3D5AFE?style=for-the-badge&logo=oauth&logoColor=white">

🧪 **Testing**  
<img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white"> <img src="https://img.shields.io/badge/Mockito-FFCD00?style=for-the-badge&logo=java&logoColor=black">

🤝 **Collaboration Tools**  
<img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white"> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white">

🚀 **Deployment**  
<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=nginx&logoColor=white"> <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white">

🔗 **External APIs**  
<img src="https://img.shields.io/badge/Kakao API-FFCD00?style=for-the-badge&logo=kakao&logoColor=black"> <img src="https://img.shields.io/badge/Toss API-0074E4?style=for-the-badge&logo=toss&logoColor=white">
