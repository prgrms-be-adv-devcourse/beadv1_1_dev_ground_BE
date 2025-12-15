-- MariaDB 초기화 스크립트
-- 외부에서 mariaId 유저로 접속 가능하도록 권한 부여

-- 모든 호스트에서 접속 가능한 mariaId 유저 생성 (이미 있으면 무시)
CREATE USER IF NOT EXISTS 'mariaId'@'%' IDENTIFIED BY 'mariaPwd';

-- dbay_user 데이터베이스에 대한 모든 권한 부여
GRANT ALL PRIVILEGES ON dbay_user.* TO 'mariaId'@'%';

-- 권한 적용
FLUSH PRIVILEGES;