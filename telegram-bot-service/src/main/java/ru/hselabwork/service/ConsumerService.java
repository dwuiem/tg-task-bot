package ru.hselabwork.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public interface ConsumerService {
    void consumeAnswerMessage(SendMessage sendMessage);
    void consumeDeleteMessage(DeleteMessage deleteMessage);
}
