# Telegram Bot 

This repository contains a Spring Boot-based Telegram bot that provides various types of content. The bot integrates with a database to serve content dynamically and offers several features that make it flexible and customizable.

## Features

The bot supports a wide range of content and functionalities, including:

- **Content Filtering**: Access to different types of content such as videos, photos, bundles, and special items.
- **Admin Commands**: Provides special commands for administrators to manage content, such as adding, removing, or editing entries.
- **Localization Support**: Switch between different languages for bot messages.
- **Dynamic Content Updates**: The bot fetches content from the database and dynamically updates the list based on user queries.
- **User-Specific Responses**: Tailors messages based on the user’s input and status (admin or regular user).
- **Content Pricing & Payment Options**: Displays content prices with integrated payment methods.
- **Notification Channels**: Sends content updates and notifications to a specified Telegram channel.

## Requirements

To get the bot up and running, you will need:

- A **Telegram Bot API token**: Create a bot through [BotFather](https://core.telegram.org/bots#botfather).
- A **Database**: The bot uses a Oracle database as its data source for content and logs. Ensure the database is properly configured.
- **Spring Boot**: The bot is built with Spring Boot and uses several features such as dependency injection, property management, and logging.

## Setup and Configuration

### Step 1: Clone the repository

```bash
git clone https://github.com/MartinBStudio/TelegramBot.git
cd telegram-bot
```

### Step 2: Setup Environment Variables

- Configure all necessary environment variables by referring to:

    - `application.properties`
    - `telegram.properties`


