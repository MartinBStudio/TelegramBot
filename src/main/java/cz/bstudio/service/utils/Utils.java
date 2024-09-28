package cz.bstudio.service.utils;

import cz.bstudio.exception.ParsingErrorException;
import cz.bstudio.service.logger.LogEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    public static Long calculateResponseTime(LogEntity logEntity) {
        var initialTime = logEntity.getTimestamp();
        var finalizedTime = LocalDateTime.now();
        return Duration.between(initialTime, finalizedTime).toMillis();
    }
    public static Map<String, String> parsePayload(String payload, String object,List<String> allowedFields) {
        var fields = Arrays.stream(payload.replace(object + "(", "").replace(")", "").split(", "))
                .map(s -> s.split("="))
                .filter(a -> a.length > 0) // Ensure there's at least a key
                .collect(Collectors.toMap(
                        a -> a[0].trim(),
                        a -> {
                            if (a.length > 1) {
                                String key = a[0].trim();
                                String value = a[1].trim();
                                if ("price".equals(key)) {
                                    try {
                                        Integer.parseInt(value); // Attempt to parse as an integer
                                    } catch (NumberFormatException e) {
                                        throw new ParsingErrorException("Invalid price format (price have to be number): " + value);
                                    }
                                }
                                return value; // Return the value if no issues
                            } else {
                                throw new ParsingErrorException("Parsing error: Missing value for key '" + a[0].trim() + "'");
                            }
                        }
                ));
        final var parsingErrorMessage = "Parsing error: No valid fields found in the payload";
        if (fields.isEmpty()) {
            throw new ParsingErrorException(parsingErrorMessage);
        }

        boolean atLeastOneFieldFound = allowedFields.stream().anyMatch(fields::containsKey);
        if (!atLeastOneFieldFound) {
            throw new ParsingErrorException(String.format("Parsing error: At least one of the required fields must be present\n %s",allowedFields));
        }
        return fields;
    }
    public static void checkLeastOneField(Map<String, String> fields) {
        boolean atLeastOnePresent = fields.values().stream().anyMatch(Objects::nonNull);
        if (!atLeastOnePresent) {
            throw new ParsingErrorException("Parsing error: At least one required field must be present.");
        }
    }


}
