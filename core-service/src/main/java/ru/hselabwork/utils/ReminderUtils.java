package ru.hselabwork.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.model.Task;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ru.hselabwork.utils.MessageUtils.REMINDER_TEXT;

public class ReminderUtils {

    public static SendMessage generateReminderMessage(Long chatId, Task task) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("%s\n\n%s".formatted(REMINDER_TEXT, TaskUtils.getTaskInfo(task)))
                .parseMode("HTML")
                .build();
    }

    public static String formatReminderList(List<Reminder> reminders) {
        if (reminders.isEmpty()) {
            return "\uD83D\uDD14 У вас нет активных напоминаний";
        }

        StringBuilder sb = new StringBuilder("📌 <b>Ваши напоминания:</b>\n\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (int i = 0; i < reminders.size(); i++) {
            Reminder reminder = reminders.get(i);
            sb.append(String.format("<b>%d. %s</b> 🕒\nЗадача: <i>%s</i>\n\n",
                    i + 1,
                    reminder.getReminderTime().format(formatter),
                    reminder.getTask().getDescription()));
        }

        return sb.toString();
    }

    public static SendMessage generateReminderListMessage(Long chatId, List<Reminder> reminders) {
        String text = formatReminderList(reminders);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> reminderButtons = new ArrayList<>();
        for (int i = 0; i < reminders.size(); i++) {
            Reminder reminder = reminders.get(i);
            InlineKeyboardButton reminderButton = new InlineKeyboardButton("Удалить %d".formatted(i + 1));
            reminderButton.setCallbackData("delete_reminder:" + reminder.getId());
            reminderButtons.add(List.of(reminderButton));
        }
        markup.setKeyboard(reminderButtons);
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(markup)
                .build();
    }
}
