FROM gradle:8.5-jdk17 AS compile
COPY . .
RUN gradle clean build --no-daemon

FROM eclipse-temurin:25
WORKDIR /home/application/java
COPY --from=compile "/home/gradle/build/libs/application.jar" .
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/home/application/java/application.jar"]