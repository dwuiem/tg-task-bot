package ru.hselabwork.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hselabwork.model.Task;

import static ru.hselabwork.utils.MessageUtils.REMINDER_TEXT;

public class ReminderUtils {

    public static SendMessage generateReminderMessage(Long chatId, Task task) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("%s\n\n%s".formatted(REMINDER_TEXT, TaskUtils.getTaskInfo(task)))
                .parseMode("HTML")
                .build();
    }
}
