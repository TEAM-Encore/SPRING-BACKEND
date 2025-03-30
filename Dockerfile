# 최신 버전의 Selenium Standalone Chrome 사용
FROM selenium/standalone-chrome:latest

# OpenJDK 17 설치
USER root
RUN apt-get update && apt-get install -y openjdk-17-jdk

# 애플리케이션 JAR 파일 추가
ADD ./build/libs/server-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java -Duser.timezone=Asia/Seoul -jar /app.jar"]