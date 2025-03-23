package ru.hselabwork.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {
    public static final String WELCOME_TEXT = "Добро пожаловать в планировщик задач";
    public static final String NO_ANSWER_TEXT = "Такого варианта не существует";
    public static final String NO_CALLBACK_TEXT = "Такого я ещё не умею";
    public static final String TASKS_NOT_FOUND_TEXT = "На текущий момент у вас <b>нет</b> никаких задач";
    public static final String REMINDER_TEXT = "⏰ <u><b>НАПОМИНАНИЕ</b></u> ⏰";
    public static final String ENTER_DESCRIPTION_TEXT = "Напиши <b>новое описание</b> к задаче";

    public static final String ADD_TASK_MESSAGE = "Добавить задачу ➕";
    public static final String TASKS_LIST_MESSAGE = "Список всех задач \uD83D\uDCCB";
    public static final String REMINDERS_MESSAGE = "Напоминания ⏰";

    public static final String TASKS_DELETED_TEXT = "✅ Задачи и все связанные с ней напоминания успешно <u>удалены</u>";
    public static final String TASK_DELETED_TEXT = "✅ Задача и все связанные с ней напоминания успешно <u>удалены</u>";
    public static final String TASK_UPDATED_TEXT = "✅ Задача успешно <u>обновлена</u>";
    public static final String TASK_CREATED_TEXT = "✅ Задача успешно <u>создана</u>";
    public static final String REMINDER_CREATED_TEXT = "✅ Напоминание успешно <u>создано</u>";

    public static final String TASK_NOT_FOUND_TEXT = "❗ Задача <u>не найдена</u>. Возможно она была удалена";
    public static final String REMINDER_BEFORE_NOW = "❗ Время напоминание не может быть раньше чем текущее. Попробуй заново";
    public static final String REMINDER_AFTER_DEADLINE = "❗ Время напоминание не может быть после дедлайна. Попробуй заново";
    public static final String WRONG_TASK_FORMAT_TEXT = "❗ Неверный формат задачи. Попробуй заново";
    public static final String WRONG_REMINDER_FORMAT_TEXT = "❗ Неверный формат времени напоминания. Попробуй заново";
    public static final String WRONG_DATE_FORMAT = "❗ Неправильный формат даты. Попробуй заново";
    public static final String WRONG_DATETIME_FORMAT_TEXT = "❗ Неправильный формат времени или даты. Попробуй заново";

    public static final String ENTER_TASK_TEXT =
            """
            <b>Опишите задачу в следующем формате</b>:
            
            <i>[0писание задачи]</i>
            <i>[Дата 01.01.1111] и/или [Время 11:11]</i>
            """;
    public static final String ENTER_REMINDER_TEXT =
            """
            Напиши <b>время</b> напоминания в формате:
            <i>[Дата 01.01.1010] и/или [Время 11:11]</i>
            """;
    public static final String ENTER_DELETING_DATE_TEXT =
            """
            Напиши <b>дату</b> на которую нужно удалить задачи:
            <i>[01.01.1111]</i>
            """;
    public static final String ENTER_NEW_DEADLINE_TEXT =
            """
            Напиши <b>время</b> нового дедлайна в формате:
            <i>[Дата 01.01.1010] и/или [Время 11:11]</i>
            """;

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
        secondRow.add(new KeyboardButton(REMINDERS_MESSAGE));

        keyboard.add(firstRow);
        keyboard.add(secondRow);

        keyboardMarkup.setKeyboard(keyboard);
        return SendMessage.builder()
                .text(WELCOME_TEXT)
                .chatId(chatId)
                .replyMarkup(keyboardMarkup)
                .build();
    }

}
