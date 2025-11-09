FROM selenium/standalone-chrome:latest

# OpenJDK 설치
USER root
RUN apt-get update && apt-get install -y openjdk-17-jdk wget unzip curl

# Chrome 버전에 맞는 ChromeDriver 설치
RUN CHROME_VERSION=$(google-chrome --version | grep -oE "[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+") && \
    echo "Detected Chrome version: $CHROME_VERSION" && \
    wget -q https://storage.googleapis.com/chrome-for-testing-public/$CHROME_VERSION/linux64/chromedriver-linux64.zip -O /tmp/chromedriver.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    mv /usr/local/bin/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver && \
    chmod +x /usr/local/bin/chromedriver && \
    rm -rf /tmp/chromedriver.zip /usr/local/bin/chromedriver-linux64

# 버전 확인
RUN chromedriver --version && google-chrome --version

# 애플리케이션 JAR 파일 추가
ADD ./build/libs/server-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java -Duser.timezone=Asia/Seoul -jar /app.jar"]
