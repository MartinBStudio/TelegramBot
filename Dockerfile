# Use OpenJDK 21 as base image
FROM alpine
# Set working directory inside the container
WORKDIR /app
# Copy the executable JAR file from the host file system to the container
COPY hookup-0.0.1.jar /app/hookup-0.0.1.jar
# Command to run the application
CMD ["java", "-jar", "hookup-0.0.1.jar"]