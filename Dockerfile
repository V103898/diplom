FROM openjdk:21-jre-slim
WORKDIR /app
COPY target/diplom-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]