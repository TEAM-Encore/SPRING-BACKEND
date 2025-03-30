FROM selenium/standalone-chrome:latest

# OpenJDK 설치
USER root
RUN apt-get update && apt-get install -y openjdk-17-jdk wget unzip

# ChromeDriver 다운로드 및 설치
ARG CHROMEDRIVER_VERSION=134.0.6998.165
RUN wget -q https://storage.googleapis.com/chrome-for-testing-public/$CHROMEDRIVER_VERSION/linux64/chromedriver-linux64.zip -O /tmp/chromedriver.zip \
    && unzip /tmp/chromedriver.zip -d /usr/local/bin/ \
    && mv /usr/local/bin/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver \
    && rm -rf /tmp/chromedriver.zip /usr/local/bin/chromedriver-linux64

# ChromeDriver 버전 확인
RUN chromedriver --version

# 애플리케이션 JAR 파일 추가
ADD ./build/libs/server-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java -Duser.timezone=Asia/Seoul -jar /app.jar"]