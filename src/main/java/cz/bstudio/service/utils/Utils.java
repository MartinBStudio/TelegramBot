package cz.bstudio.service.utils;

import cz.bstudio.service.logger.LogEntity;

import java.time.Duration;
import java.time.LocalDateTime;

public class Utils {
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    public static Long calculateResponseTime(LogEntity logEntity) {
        var initialTime = logEntity.getTimestamp();
        var finalizedTime = LocalDateTime.now();
        return Duration.between(initialTime, finalizedTime).toMillis();
    }


}
