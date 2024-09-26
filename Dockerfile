FROM openjdk:17
ENV ARTIFACT_NAME=telegram-content-provider-bot.jar
# Telegram bot environment variables
ENV TELEGRAM_BOT_USERNAME=your-telegram-bot-username
ENV TELEGRAM_BOT_LOACALIZATION=localization
ENV ORACLE_DB_USERNAME=your-db-username
ENV ORACLE_DB_PASSWORD=your-db-password
# Set the server port
ENV SERVER_PORT=5000
# Copy the jar file from the build/libs directory
COPY build/libs/$ARTIFACT_NAME /$ARTIFACT_NAME
# Set the maintainer label
LABEL maintainer="Martin Masika <martin.masika@icloud.com>"
# Run the jar file using the ARTIFACT_NAME variable
CMD ["sh", "-c", "java -jar /$ARTIFACT_NAME"]