package ru.hselabwork.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.hselabwork.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

    public static final String WELCOME_TEXT = "Добро пожаловать";
    public static final String NO_ANSWER_TEXT = "Такого варианта не существует";
    public static final String NO_CALLBACK_TEXT = "Такого я ещё не умею";
    public static final String TASK_NOT_FOUND_TEXT = "❗Задача <u>не найдена</u>. Возможно она была удалена";
    public static final String WRONG_TASK_FORMAT_TEXT = "❗ Неверный формат задачи. Попробуй заново";
    public static final String TASK_DELETED_TEXT = "✅ Задача успешно <u>удалена</u>";
    public static final String TASK_UPDATED_TEXT = "✅ Задача успешно <u>обновлена</u>";
    public static final String TASK_CREATED_TEXT = "✅ Задача успешно <u>создана</u>";
    public static final String WRONG_REMINDER_FORMAT_TEXT = "❗ Неверный формат времени напоминания. Попробуй заново";
    public static final String REMINDER_CREATED_TEXT = "✅ Напоминание успешно <u>создано</u>";
    public static final String TASKS_NOT_FOUND_TEXT = "На текущий момент у вас <b>нет</b> никаких задач";
    public static final String REMINDER_TEXT = "⏰ <u><b>НАПОМИНАНИЕ</b></u> ⏰";
    public static final String ENTER_TASK_TEXT =
            """
            <b>Опишите задачу в следующем формате</b>:
            
            <i>[0писание задачи]</i>
            <i>[Дата 01.01.1111] и/или [Время 11:11]</i>
            """;
    public static final String ENTER_DESCRIPTION_TEXT = "Напиши <b>новое описание</b> к задаче";
    public static final String ENTER_REMINDER_TEXT =
            """
            Напиши <b>время</b> напоминания в формате:
            <i>[Дата 01.01.1010] и/или [Время 11:11]</i>
            """;
    public static final String TASKS_TODAY_MESSAGE = "Задачи сегодня \uD83D\uDCC6";
    public static final String ADD_TASK_MESSAGE = "Добавить задачу ➕";
    public static final String TASKS_LIST_MESSAGE = "Список всех задач \uD83D\uDCCB";
    public static final String REMINDERS_MESSAGE = "Напоминания ⏰";
    public static final String DELETE_COMPLETED_TASKS_MESSAGE = "Удалить выполненные задачи \uD83D\uDDD1️";

    public static SendMessage generateReminderMessage(Long chatId, Task task) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("%s\n\n%s".formatted(REMINDER_TEXT, TaskUtils.getTaskInfo(task)))
                .parseMode("HTML")
                .build();
    }

    public static SendMessage generateEnterReminderMessage(Long chatId) {
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Отменить");
        cancelButton.setCallbackData("cancel:");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(List.of(List.of(cancelButton)));

        return SendMessage.builder()
                .chatId(chatId)
                .text(ENTER_REMINDER_TEXT)
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
                .text(ENTER_DESCRIPTION_TEXT)
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

    public static SendMessage generateWelcomeMessage(Long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(new KeyboardButton(TASKS_LIST_MESSAGE));

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(new KeyboardButton(ADD_TASK_MESSAGE));
        secondRow.add(new KeyboardButton(DELETE_COMPLETED_TASKS_MESSAGE));

        KeyboardRow thirdRow = new KeyboardRow();
        thirdRow.add(new KeyboardButton(REMINDERS_MESSAGE));
        thirdRow.add(new KeyboardButton(TASKS_TODAY_MESSAGE));

        keyboard.add(firstRow);
        keyboard.add(secondRow);
        keyboard.add(thirdRow);

        keyboardMarkup.setKeyboard(keyboard);
        return SendMessage.builder()
                .text(WELCOME_TEXT)
                .chatId(chatId)
                .replyMarkup(keyboardMarkup)
                .build();
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
