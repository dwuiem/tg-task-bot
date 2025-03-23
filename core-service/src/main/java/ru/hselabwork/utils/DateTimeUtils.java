package ru.hselabwork.utils;



import ru.hselabwork.exception.WrongDateTimeException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtils {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final Pattern DATETIME_PATTERN = Pattern.compile(
            "(\\d{2}\\.\\d{2}\\.\\d{4})?" +
                    "(?:\\s*(\\d{2}:\\d{2}))?"
    );

    public static LocalDateTime getCurrentMoscowTime() {
        ZonedDateTime moscowTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        return moscowTime.toLocalDateTime();
    }

    public static LocalDate parseDateFromText(String text) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(text, formatter);
    }

    public static LocalDateTime parseDateTimeFromText(String text) throws WrongDateTimeException {
        Matcher matcher = DATETIME_PATTERN.matcher(text);
        if (!matcher.matches())
            throw new WrongDateTimeException("Invalid message format: " + text);

        String dateStr = matcher.group(1);
        String timeStr = matcher.group(2);

        if (dateStr == null && timeStr == null) {
            throw new WrongDateTimeException("Invalid message format: " + text);
        }
        try {
            LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr, DATE_FORMATTER) : getCurrentMoscowTime().toLocalDate();
            LocalTime time = (timeStr != null) ? LocalTime.parse(timeStr, TIME_FORMATTER) : LocalTime.of(23, 59);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException e) {
            throw new WrongDateTimeException("Invalid message format: " + text);
        }
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
