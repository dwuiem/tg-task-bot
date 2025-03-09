package ru.hselabwork.dispatcher.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public interface AnswerConsumer {
    void consumeAnswerMessage(SendMessage sendMessage);
    void consumeDeleteMessage(DeleteMessage deleteMessage);
}
