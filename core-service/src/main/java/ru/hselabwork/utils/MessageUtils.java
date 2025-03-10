package ru.hselabwork.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.hselabwork.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

    private static final String noTasksResponse = "На текущий момент у вас <b>нет</b> никаких задач";
    private static final String taskDeletedResponse = "✅ Задача успешно <u>удалена</u>";
    private static final String noTaskFoundResponse = "❗Задача <u>не найдена</u>. Возможно она была удалена";
    private static final String taskUpdated = "✅ Задача <u>обновлена</u>";

    public static SendMessage generateTaskUpdatedMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(taskUpdated)
                .parseMode("HTML")
                .build();
    }

    public static SendMessage generateTaskDeletedMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(taskDeletedResponse)
                .parseMode("HTML")
                .build();
    }

    public static SendMessage generateTaskNotFoundMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(noTaskFoundResponse)
                .parseMode("HTML")
                .build();
    }

    public static SendMessage generateTaskInfoMessage(Task task, Long chatId) {
        String text = TaskUtils.getFullTaskInfo(task);

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

        rows.add(List.of(deleteButton, completeButton));
        rows.add(List.of(editDescription));
        rows.add(List.of(editDate));
        rows.add(List.of(editTime));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);

        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(markup)
                .build();
    }

    public static SendMessage generateTaskListMessage(List<Task> tasks, Long chatId) {

        if (tasks.isEmpty()) {
            return SendMessage.builder()
                    .text(noTasksResponse)
                    .parseMode("HTML")
                    .build();
        }

        String responseText = MessageUtils.getTaskListInfo(tasks);

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
                .text(responseText)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();

    }

    // Parse small information from task

    private static String getTaskInfo(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getDescription());
        if (task.getDeadline() != null) {
            sb.append(String.format("\n⏳ <u>%s</u>", TaskUtils.parseDateTime(task.getDeadline())));
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
