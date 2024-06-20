FROM openjdk:21-jdk-slim
COPY build/libs/hookapp-0.0.1.jar hookapp-0.0.1.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "/hookapp-0.0.1.jar"]

