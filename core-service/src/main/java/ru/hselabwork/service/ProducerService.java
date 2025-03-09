package ru.hselabwork.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public interface ProducerService {
    void produceAnswer(SendMessage sendMessage);
    void produceDelete(DeleteMessage deleteMessage);
}
