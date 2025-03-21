package ru.hselabwork.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.model.Reminder;

public interface ConsumerService {
    void consumeMessage(Message message);
    void consumeCallbackQuery(CallbackQuery callbackQuery);
    void consumeExpiredReminder(Reminder reminder);
}
