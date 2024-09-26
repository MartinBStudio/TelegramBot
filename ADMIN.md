# ADMIN Operation Guide

This guide provides an overview of the available bot commands for managing bot settings, content, and general tasks.
Only authorized users (admins) can execute these commands. To become an admin, provide your username to the bot owner.

---

## General Commands

### Command: `/IS_ADMIN`

- **Description:** Checks if the current Telegram user is an admin.
- **Response (Success):** `true` (if the user is an admin) or `false` (if the user is not an admin).

---

### Command: `/CHANGE_LANGUAGE`

- **Description:** Switches the bot language between Czech and English.
- **Response (Success):** `Bot language switched to Czech` or `Bot language switched to English`.

---

## Bot-Related Commands

### Command: `/DISPLAY_BOT`

- **Description:** Displays the current bot details, including the seller name and payment methods.
- **Response (Success):**
  `BotEntity(sellerName=YourBotName, paymentMethod1=YourPaymentMethod1, paymentMethod2=YourPaymentMethod2)`.

---

### Command: `/EDIT_BOT_[BotEntity(...)]`

- **Example:** `/EDIT_BOT_[BotEntity(sellerName=John)]`
- **Description:** Edits bot details such as `sellerName`, `paymentMethod1`, or `paymentMethod2`.
- **Response (Success):** Success message along with the updated bot details.
- **Response (Failure):** Invalid syntax or insufficient permissions.

---

## Content-Related Commands

### Command: `/REMOVE_ID`

- **Example:** `/REMOVE_10`
- **Description:** Removes content associated with the given ID.
- **Response (Success):** `10 – My Video – REMOVED`.
- **Response (Failure):** Not found or insufficient permissions.

---

### Command: `/DISPLAY_ID`

- **Example:** `/DISPLAY_10`
- **Description:** Displays editable content details for the given ID.
- **Response (Success):** Content details are displayed.
- **Response (Failure):** Not found or insufficient permissions.

---

### Command: `/ADD_[...]`

- **Example:** `/ADD_[ContentEntity(name=My Name, type=My Type)]`
- **Description:** Adds new content by providing details such as `name`, `type`, `subType`, `description`, `price`,
  `previewUrl`, and `fullUrl`. No fields are mandatory, but all provided fields must be separated by a comma and space (
  `, `).
- **Response (Success):** New content added – with the content details displayed.
- **Response (Failure):** Failed to add new content.

---

### Command: `/EDIT_[...]`

- **Example:** `/EDIT_105_[ContentEntity(price=658)]`
- **Description:** Edits existing content by providing one or more updated details (e.g., price, name, type). Multiple
  fields must be separated by a comma and space (`, `).
- **Response (Success):** Success message along with the updated content details.
- **Response (Failure):** Not found or insufficient permissions.
