package ru.hselabwork.utils;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtils {
    public static LocalDateTime getCurrentMoscowTime() {
        ZonedDateTime moscowTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        return moscowTime.toLocalDateTime();
    }

    public static String getTimeRemaining(LocalDateTime now, LocalDateTime deadline) {
        Duration duration = Duration.between(now, deadline);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        return String.format("%d день(дня) %d час(ов) %d мин(ут) %d сек(унд)", days, hours, minutes, seconds);
    }

}
