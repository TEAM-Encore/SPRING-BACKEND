FROM selenium/standalone-chrome:114.0

# OpenJDK 설치
USER root
RUN apt-get update && apt-get install -y openjdk-17-jdk

# ChromeDriver 다운로드 및 설치
RUN wget https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip -O /tmp/chromedriver.zip
RUN unzip /tmp/chromedriver.zip -d /usr/local/bin/
RUN chmod +x /usr/local/bin/chromedriver

# 애플리케이션 jar 파일 추가
ADD ./build/libs/server-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java -Duser.timezone=Asia/Seoul -jar /app.jar"]