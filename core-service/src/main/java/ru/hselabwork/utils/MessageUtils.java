package ru.hselabwork.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.hselabwork.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

    public static final String noAnswerMessage = "Такого варианта не существует";
    public static final String noCallbackMessage = "Такого я ещё не умею";
    public static final String taskNotFoundText = "❗Задача <u>не найдена</u>. Возможно она была удалена";
    public static final String wrongTaskFormatText = "❗ Неверный формат задачи. Попробуй заново";
    public static final String wrongReminderFormatText = "❗ Неверный формат времени напоминания. Попробуй заново";
    public static final String taskDeletedText = "✅ Задача успешно <u>удалена</u>";
    public static final String taskUpdatedText = "✅ Задача успешно <u>обновлена</u>";
    public static final String taskCreatedText = "✅ Задача успешно <u>создана</u>";
    public static final String reminderCreatedText = "✅ Напоминание успешно <u>создано</u>";

    private static final String tasksNotFoundText = "На текущий момент у вас <b>нет</b> никаких задач";

    private static final String reminderText = "⏰ <u><b>НАПОМИНАНИЕ</b></u> ⏰";

    public static final String enterTaskText =
            """
            <b>Опишите задачу в следующем формате</b>:
            
            <i>[0писание задачи]</i>
            <i>[Дата 01.01.1111] [Время 11:11]</i>
            """;

    public static final String enterDescriptionMessage = "Напиши <b>новое описание</b> к задаче";
    public static final String enterReminderMessage = "Напиши <b>время</b> напоминания";

    public static SendMessage generateReminderMessage(Long chatId, Task task) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("%s\n\n%s".formatted(reminderText, getTaskInfo(task)))
                .parseMode("HTML")
                .build();
    }

    public static SendMessage generateEnterReminderMessage(Long chatId) {
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Отменить");
        cancelButton.setCallbackData("cancel:");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(List.of(List.of(cancelButton)));

        return SendMessage.builder()
                .chatId(chatId)
                .text(enterReminderMessage)
                .parseMode("HTML")
                .replyMarkup(keyboardMarkup)
                .build();
    }

    public static SendMessage generateEnterDescriptionMessage(Long chatId) {
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Отменить");
        cancelButton.setCallbackData("cancel:");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(List.of(List.of(cancelButton)));

        return SendMessage.builder()
                .chatId(chatId)
                .text(enterDescriptionMessage)
                .parseMode("HTML")
                .replyMarkup(keyboardMarkup)
                .build();
    }

    public static SendMessage generateSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("HTML")
                .build();
    }

    public static SendMessage generateTaskInfoMessage(Task task, Long chatId) {
        String detailedTaskInfo = TaskUtils.getDetailedTaskInfo(task);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton deleteButton = new InlineKeyboardButton("Удалить \uD83D\uDDD1\uFE0F");
        deleteButton.setCallbackData("delete_task:" + task.getId());

        InlineKeyboardButton completeButton = new InlineKeyboardButton(task.isCompleted() ? "Убрать ✅" : "✅");
        completeButton.setCallbackData("complete_task:" + task.getId());

        InlineKeyboardButton editDescription = new InlineKeyboardButton("Изменить описание");
        editDescription.setCallbackData("edit_description:" + task.getId());

        InlineKeyboardButton editDate = new InlineKeyboardButton("Изменить дату");
        editDate.setCallbackData("edit_date:" + task.getId());

        InlineKeyboardButton editTime = new InlineKeyboardButton("Изменить время");
        editTime.setCallbackData("edit_time:" + task.getId());

        InlineKeyboardButton addReminder = new InlineKeyboardButton("Добавить напоминание ⏰");
        addReminder.setCallbackData("add_reminder:" + task.getId());


        rows.add(List.of(deleteButton, completeButton));
        rows.add(List.of(editDescription));
        rows.add(List.of(editDate, editTime));
        rows.add(List.of(addReminder));

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
                    .text(tasksNotFoundText)
                    .chatId(chatId)
                    .parseMode("HTML")
                    .build();
        }

        String taskListInfo = MessageUtils.getTaskListInfo(tasks);

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

    // Parse small information from task
    // TODO Доделать

    private static String getTaskInfo(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getDescription());
        LocalDateTime now = DateTimeUtils.getCurrentMoscowTime();
        if (task.getDeadline() != null) {
            sb.append(String.format("\n⏳ <u>%s</u>", TaskUtils.parseDateTime(task.getDeadline())));
            if (!task.isCompleted()) {
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

    private static String getTaskListInfo(List<Task> tasks) {
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
