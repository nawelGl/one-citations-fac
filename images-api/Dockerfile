FROM gradle:8.5-jdk17 AS compile
WORKDIR /home/gradle
COPY . .
RUN gradle clean build --no-daemon -x test

FROM eclipse-temurin:25
WORKDIR /home/application/java
COPY --from=compile /home/gradle/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app.jar"]