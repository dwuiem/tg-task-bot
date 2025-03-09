package ru.hselabwork.utils;

import ru.hselabwork.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Pattern PATTERN = Pattern.compile("(.+?)(?:\\n(\\d{2}\\.\\d{2}\\.\\d{4}))?(?:\\s*(\\d{2}:\\d{2}))?");


    public static Task parseTaskFromMessage(String message) throws DateTimeParseException {
        Matcher matcher = PATTERN.matcher(message);
        if (matcher.matches()) {
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
        return null;
    }

    public static String getTaskInfo(Task task) {
        return task.getDescription() + (task.getDeadline() != null ? ("\n⏳ " + task.getDeadline().format(DATE_TIME_FORMATTER)) : "");
    }

    public static String getFullTaskInfo(Task task) {
        return "\uD83D\uDCCC *Задача*\n\n" +
                "\uD83D\uDD39 _Описание_: " + task.getDescription() + "\n\n" +
                "_Статус:_ " + (task.isCompleted() ? "✅" : "\uD83D\uDD34") + "\n\n" +
                (task.getDeadline() != null ? ("⏳ " + task.getDeadline().format(DATE_TIME_FORMATTER)) + "\n" : "") +
                "Создан: " + task.getCreated().format(DATE_TIME_FORMATTER);
    }

    public static String getTaskListInfo(List<Task> tasks) {
        StringBuilder sb = new StringBuilder("\uD83D\uDD39 Ваш список задач:\n\n");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.isCompleted()) {
                sb.append("~~");
            }
            sb.append(i + 1).append(". ").append(getTaskInfo(task));
            if (task.isCompleted()) {
                sb.append("~~");
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }
}
