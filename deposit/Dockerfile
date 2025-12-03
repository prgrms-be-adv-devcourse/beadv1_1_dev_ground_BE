#base이미지
FROM eclipse-temurin:21-jdk

# UTF-8, 타임존 설정
ENV TZ=Asia/Seoul
ENV LANG=ko_KR.UTF-8
ENV LC_ALL=ko_KR.UTF-8

# 작업 디렉토리
WORKDIR /app

# 빌드된 jar 복사
#COPY build/libs/eureka.jar app.jar
COPY build/libs/*.jar app.jar


#실행
ENTRYPOINT ["java", "-jar", "app.jar"]