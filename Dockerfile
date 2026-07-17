FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle* .
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q
COPY src src
RUN ./gradlew build -x test --no-daemon -q

FROM eclipse-temurin:17-jre
WORKDIR /app
# gradle이 실행용 jar와 -plain.jar를 함께 만들므로 plain을 제외하고 하나만 고른다
COPY --from=builder /app/build/libs/*.jar /app/libs/
RUN mv "$(ls /app/libs/*.jar | grep -v -- '-plain.jar')" /app/app.jar && rm -rf /app/libs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
