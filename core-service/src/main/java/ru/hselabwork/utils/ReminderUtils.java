package ru.hselabwork.utils;

import ru.hselabwork.exception.ReminderParseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReminderUtils {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final Pattern PATTERN = Pattern.compile(
            "(\\d{2}\\.\\d{2}\\.\\d{4})?" +
                    "(?:\\s*(\\d{2}:\\d{2}))?"
    );

    public static LocalDateTime parseDateTimeFromMessage(String message) throws ReminderParseException {
        Matcher matcher = PATTERN.matcher(message);
        if (!matcher.matches())
            throw new ReminderParseException("Invalid message format: " + message);

        String dateStr = matcher.group(1);
        String timeStr = matcher.group(2);

        if (dateStr == null && timeStr == null) {
            throw new ReminderParseException("Invalid message format: " + message);
        }
        try {
            LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr, DATE_FORMATTER) : LocalDate.now();
            LocalTime time = (timeStr != null) ? LocalTime.parse(timeStr, TIME_FORMATTER) : LocalTime.of(23, 59);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException e) {
            throw new ReminderParseException("Invalid message format: " + message);
        }
    }
}
