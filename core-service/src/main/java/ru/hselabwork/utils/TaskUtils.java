package ru.hselabwork.utils;

import ru.hselabwork.exception.TaskDescriptionParseException;
import ru.hselabwork.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Pattern PATTERN = Pattern.compile("(.+?)(?:\\n(\\d{2}\\.\\d{2}\\.\\d{4}))?(?:\\s*(\\d{2}:\\d{2}))?");


    public static Task parseTaskFromMessage(String message) throws Exception {
        Matcher matcher = PATTERN.matcher(message);
        if (!matcher.matches())
            throw new TaskDescriptionParseException("Invalid message format: %s".formatted(message));

        String description = matcher.group(1).trim();
        String dateStr = matcher.group(2);
        String timeStr = matcher.group(3);

        LocalDateTime deadline = null;

        if (dateStr != null || timeStr != null) {
                LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr, DATE_FORMATTER) : LocalDate.now();
                LocalTime time = (timeStr != null) ? LocalTime.parse(timeStr, TIME_FORMATTER) : LocalTime.of(23, 59);
                deadline = LocalDateTime.of(date, time);
        }

        return Task.builder()
                .description(description)
                .completed(false)
                .deadline(deadline)
                .build();

    }

    public static String parseDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String getDetailedTaskInfo(Task task) {
        StringBuilder sb = new StringBuilder();

        sb.append("\uD83D\uDCCC <b>Задача</b>\n\n");
        sb.append(String.format("\uD83D\uDD39 <i>Описание:</i> %s\n\n", task.getDescription()));
        sb.append(String.format("<i>Статус:</i> %s\n\n", (task.isCompleted() ? "✅" : "\uD83D\uDD34")));
        if (task.getDeadline() != null) {
            sb.append(String.format("⏳ %s\n", task.getDeadline().format(DATE_TIME_FORMATTER)));
        }
        sb.append(String.format("Создан: %s", task.getCreated().format(DATE_TIME_FORMATTER)));

        return sb.toString();
    }

}
