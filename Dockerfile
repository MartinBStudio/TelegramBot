FROM openjdk:17
ENV ARTIFACT_NAME=telegramBot.jar
# Telegram bot environment variables
ENV TELEGRAM_BOT_USERNAME=your-telegram-bot-username
ENV TELEGRAM_BOT_LOACALIZATION=localization
ENV ORACLE_DB_USERNAME=your-db-username
ENV ORACLE_DB_PASSWORD=your-db-password
ENV SERVER_PORT=5000
# Copy the jar file
COPY /*.jar ./$ARTIFACT_NAME
# Set the maintainer label
LABEL maintainer="Martin Masika <martin.masika@icloud.com>"
# Run the jar file
CMD ["java", "-jar", "telegramBot.jar"]
