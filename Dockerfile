# Build Stage: Gradle Build
FROM gradle:7.6-jdk17 AS build
COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon

# Runtime Stage: OpenJDK and ChromeDriver setup
FROM openjdk:17-slim
EXPOSE 22040

# Install necessary packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    gnupg \
    curl \
    libglib2.0-0 \
    libnss3 \
    libgconf-2-4 \
    libfontconfig1 \
    libx11-6 \
    libx11-xcb1 \
    libxcb1 \
    libxcomposite1 \
    libxcursor1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxi6 \
    libxrandr2 \
    libxrender1 \
    libxss1 \
    libxtst6 \
    fonts-liberation \
    libappindicator3-1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    default-mysql-client \
    sqlite3 \
    libsqlite3-dev \
    && rm -rf /var/lib/apt/lists/*

# Install Chrome
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update && apt-get install -y google-chrome-stable=129.0.6668.58-1 \
    && rm -rf /var/lib/apt/lists/*

# Install ChromeDriver
RUN CHROME_VERSION=$(google-chrome --no-sandbox --version | awk '{print $3}' | awk -F. '{print $1}') \
    && CHROMEDRIVER_VERSION=$(curl -s "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROME_VERSION") \
    && wget -q --continue -P /chromedriver "https://storage.googleapis.com/chrome-for-testing-public/129.0.6668.58/linux64/chromedriver-linux64.zip" \
    && unzip /chromedriver/chromedriver-linux64.zip -d /usr/bin/ \
    && mv /usr/bin/chromedriver-linux64/chromedriver /usr/bin/chromedriver \
    && rm -rf /chromedriver /usr/bin/chromedriver-linux64 \
    && chmod +x /usr/bin/chromedriver

# Copy built JAR file from the build stage
ARG JAR_FILE=./build/libs/server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Create necessary directories and set permissions
RUN mkdir -p /app/citymart_backup && chmod 777 /app/citymart_backup
RUN mkdir -p /app/sqlite && touch /app/sqlite/pos.db && chmod 664 /app/sqlite/pos.db

# Set environment variables for ChromeDriver and Chrome binary
ENV SPRING_SECOND_DATASOURCE_JDBCURL=jdbc:sqlite:/app/sqlite/pos.db
ENV JAVA_OPTS="-Dwebdriver.chrome.driver=/usr/bin/chromedriver"
ENV CHROME_BIN=/usr/bin/google-chrome

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]