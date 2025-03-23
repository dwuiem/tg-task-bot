package ru.hselabwork.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.hselabwork.exception.TaskDescriptionParseException;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.hselabwork.utils.DateTimeUtils.getCurrentMoscowTime;
import static ru.hselabwork.utils.MessageUtils.TASKS_NOT_FOUND_TEXT;

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
                LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr, DATE_FORMATTER) : getCurrentMoscowTime().toLocalDate();
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
                LocalDateTime now = getCurrentMoscowTime();
                if (task.getDeadline().isAfter(now)) {
                    sb.append(String.format("\n До дедлайна осталось: %s\n\n", DateTimeUtils.getTimeRemaining(now, task.getDeadline())));
                } else {
                    sb.append("\n <b>⚠ Просрочено!</b>\n\n");
                }
            }
        }
        if (!task.getReminders().isEmpty()) {
            sb.append("<b>Напоминания</b> ⏰\n\n");
            for (int i = 0; i < task.getReminders().size(); i++) {
                Reminder reminder = task.getReminders().get(i);
                sb.append("%d) %s\n".formatted(i + 1, reminder.getReminderTime().format(DATE_TIME_FORMATTER)));
            }
        }
        sb.append(String.format("\nЗадача создана %s", task.getCreated().format(DATE_TIME_FORMATTER)));

        return sb.toString();
    }

    // Parse small information from task

    public static String getTaskInfo(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getDescription());
        if (task.getDeadline() != null) {
            sb.append(String.format("\n⏳ <u>%s</u>", TaskUtils.parseDateTime(task.getDeadline())));
            if (!task.isCompleted()) {
                LocalDateTime now = getCurrentMoscowTime();
                if (task.getDeadline().isAfter(now)) {
                    sb.append(String.format("\n До дедлайна осталось: %s", DateTimeUtils.getTimeRemaining(now, task.getDeadline())));
                } else {
                    sb.append("\n <b>⚠ Просрочено!</b>");
                }
            }
            if (!task.getReminders().isEmpty()) {
                sb.append("\n⏰ - %d".formatted(task.getReminders().size()));
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

    public static SendMessage generateTaskInfoMessage(Task task, Long chatId) {
        String detailedTaskInfo = TaskUtils.getDetailedTaskInfo(task);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton deleteButton = new InlineKeyboardButton("Удалить \uD83D\uDDD1️");
        deleteButton.setCallbackData("delete_task:" + task.getId());

        InlineKeyboardButton completeButton = new InlineKeyboardButton(task.isCompleted() ? "Убрать ✅" : "✅");
        completeButton.setCallbackData("complete_task:" + task.getId());

        InlineKeyboardButton editDescription = new InlineKeyboardButton("Изменить описание");
        editDescription.setCallbackData("edit_description:" + task.getId());

        InlineKeyboardButton editDateTime = new InlineKeyboardButton("Изменить дату и время");
        editDateTime.setCallbackData("edit_datetime:" + task.getId());

        InlineKeyboardButton addReminder = new InlineKeyboardButton("Добавить напоминание ⏰");
        addReminder.setCallbackData("add_reminder:" + task.getId());

        rows.add(List.of(deleteButton, completeButton));
        rows.add(List.of(editDescription, editDateTime));
        rows.add(List.of(addReminder));

        if (task.getDeadline() != null) {
            InlineKeyboardButton deleteDeadline = new InlineKeyboardButton("Убрать дедлайн");
            deleteDeadline.setCallbackData("delete_deadline:" + task.getId());
            rows.add(List.of(deleteDeadline));
        }

        if (task.getDeadline() != null && task.getDeadline().isAfter(getCurrentMoscowTime())) {
            InlineKeyboardButton extendDeadline = new InlineKeyboardButton("Продлить задачу");
            extendDeadline.setCallbackData("extend_deadline:" + task.getId());
            rows.add(List.of(extendDeadline));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);

        return SendMessage.builder()
                .chatId(chatId)
                .text(detailedTaskInfo)
                .parseMode("HTML")
                .replyMarkup(markup)
                .build();
    }

    public static SendMessage generateTaskListMessage(List<Task> tasks, Long chatId) {

        if (tasks.isEmpty()) {
            return SendMessage.builder()
                    .text(TASKS_NOT_FOUND_TEXT)
                    .chatId(chatId)
                    .parseMode("HTML")
                    .build();
        }

        String taskListInfo = TaskUtils.getTaskListInfo(tasks);

        // Creating list markup

        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(i + 1));
            button.setCallbackData("view_task:" + task.getId().toHexString());

            row.add(button);
        }
        var markup = new InlineKeyboardMarkup(List.of(row));

        return SendMessage.builder()
                .chatId(chatId)
                .text(taskListInfo)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();

    }
}
