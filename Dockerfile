FROM openjdk:19
ENV ARTIFACT_NAME=001.jar
# Telegram bot environment variables
ENV TELEGRAM_BOT_TOKEN=your-telegram-bot-token
ENV TELEGRAM_BOT_USERNAME=your-telegram-bot-username
ENV TELEGRAM_BOT_NOTIFICATION_CHANNEL_ID=your-channel-id
ENV ORACLE_DB_USERNAME=your-db-username
ENV ORACLE_DB_PASSWORD=your-db-password
ENV TELEGRAM_BOT_ADMIN_USERS_LIST=user1,user2
# Set the server port
ENV SERVER_PORT=5000
# Copy the jar file
COPY /*.jar ./$ARTIFACT_NAME
# Set the maintainer label
LABEL maintainer="Martin Masika <martin.masika@icloud.com>"
# Run the jar file
CMD ["java", "-jar", "001.jar"]
