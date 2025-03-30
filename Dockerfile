FROM openjdk:17.0.1-jdk-slim

# 필수 패키지 설치
RUN apt-get -y update \
    && apt-get -y install wget unzip curl \
    && apt-get clean

# Google Chrome 설치
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get -y install ./google-chrome-stable_current_amd64.deb || apt-get -y -f install

# ChromeDriver 설치
RUN wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/$(curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE)/chromedriver_linux64.zip \
    && unzip /tmp/chromedriver.zip chromedriver -d /usr/bin \
    && rm /tmp/chromedriver.zip  # 설치 후 압축파일 삭제
# 애플리케이션 jar 파일 추가
ADD ./build/libs/server-0.0.1-SNAPSHOT.jar /app.jar

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java -Duser.timezone=Asia/Seoul -jar /app.jar"]