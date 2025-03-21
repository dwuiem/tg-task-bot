package ru.hselabwork.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import ru.hselabwork.model.Reminder;

public interface ProducerService {
    void produceAnswer(SendMessage sendMessage);
    void produceDelete(DeleteMessage deleteMessage);
    void produceReminder(Reminder reminder, long reminderTimeInSeconds);
}
