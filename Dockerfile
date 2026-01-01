FROM eclipse-temurin:17-jre-jammy

COPY ./build/libs/server-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["sh", "-c", "java -Duser.timezone=Asia/Seoul -jar /app.jar"]
