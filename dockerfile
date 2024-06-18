FROM openjdk:22-jdk-slim
COPY target/hookapp-0.0.1.jar hookapp-0.0.1.jar
ENTRYPOINT ["java", "-jar", "/hookapp-0.0.1.jar"]

