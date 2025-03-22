package ru.hselabwork.utils;

import ru.hselabwork.exception.TaskDescriptionParseException;
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

    public static Task parseTaskFromMessage(String message) throws TaskDescriptionParseException {
        Matcher matcher = PATTERN.matcher(message);
        if (!matcher.matches())
            throw new TaskDescriptionParseException("Invalid message format: %s".formatted(message));

        String description = matcher.group(1).trim();
        String dateStr = matcher.group(2);
        String timeStr = matcher.group(3);

        LocalDateTime deadline = null;
        try {
            if (dateStr != null || timeStr != null) {
                LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr, DATE_FORMATTER) : LocalDate.now();
                LocalTime time = (timeStr != null) ? LocalTime.parse(timeStr, TIME_FORMATTER) : LocalTime.of(23, 59);
                deadline = LocalDateTime.of(date, time);
            }
        } catch (DateTimeParseException e) {
            throw new TaskDescriptionParseException("Invalid message format: %s".formatted(message));
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
            if (!task.isCompleted()) {
                LocalDateTime now = DateTimeUtils.getCurrentMoscowTime();
                if (task.getDeadline().isAfter(now)) {
                    sb.append(String.format("\n До дедлайна осталось: %s", DateTimeUtils.getTimeRemaining(now, task.getDeadline())));
                } else {
                    sb.append("\n <b>⚠ Просрочено!</b>");
                }
            }
        }
        sb.append(String.format("Создан: %s", task.getCreated().format(DATE_TIME_FORMATTER)));

        return sb.toString();
    }

    // Parse small information from task

    public static String getTaskInfo(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getDescription());
        if (task.getDeadline() != null) {
            sb.append(String.format("\n⏳ <u>%s</u>", TaskUtils.parseDateTime(task.getDeadline())));
            if (!task.isCompleted()) {
                LocalDateTime now = DateTimeUtils.getCurrentMoscowTime();
                if (task.getDeadline().isAfter(now)) {
                    sb.append(String.format("\n До дедлайна осталось: %s", DateTimeUtils.getTimeRemaining(now, task.getDeadline())));
                } else {
                    sb.append("\n <b>⚠ Просрочено!</b>");
                }
            }
        }

        return sb.toString();
    }

    // Parse information from list of tasks

    public static String getTaskListInfo(List<Task> tasks) {
        StringBuilder sb = new StringBuilder("\uD83D\uDD39 Ваш список задач:\n\n");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.isCompleted()) {
                sb.append(String.format("<s>%d. %s</s>\n\n", i + 1, getTaskInfo(task)));
            } else {
                sb.append(String.format("%d. %s\n\n", i + 1, getTaskInfo(task)));
            }
        }
        return sb.toString();
    }
}
