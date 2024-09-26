package cz.bstudio.service;

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
        long responseTimeInMillis = Duration.between(initialTime, finalizedTime).toMillis();
        return responseTimeInMillis;
    }


}
