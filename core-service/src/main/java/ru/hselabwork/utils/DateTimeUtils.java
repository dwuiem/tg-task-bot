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

        StringBuilder result = new StringBuilder();

        if (days > 0) result.append(days).append(" ").append(getDayDeclension(days)).append(" ");
        if (hours > 0) result.append(hours).append(" ").append(getHourDeclension(hours)).append(" ");
        if (minutes > 0) result.append(minutes).append(" ").append(getMinuteDeclension(minutes)).append(" ");
        if (seconds > 0) result.append(seconds).append(" ").append(getSecondDeclension(seconds)).append(" ");

        return result.isEmpty() ? "0 секунд" : result.toString().trim();
    }

    private static String getDayDeclension(long n) {
        if (n % 10 == 1 && n % 100 != 11) return "день";
        if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) return "дня";
        return "дней";
    }

    private static String getHourDeclension(long n) {
        if (n % 10 == 1 && n % 100 != 11) return "час";
        if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) return "часа";
        return "часов";
    }

    private static String getMinuteDeclension(long n) {
        if (n % 10 == 1 && n % 100 != 11) return "минута";
        if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) return "минуты";
        return "минут";
    }

    private static String getSecondDeclension(long n) {
        if (n % 10 == 1 && n % 100 != 11) return "секунда";
        if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) return "секунды";
        return "секунд";
    }

}
