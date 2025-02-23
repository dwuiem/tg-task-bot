package ru.hselabwork.dispatcher.service.impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hselabwork.dispatcher.service.AnswerConsumer;

public class AnswerConsumerImpl implements AnswerConsumer {

    @Override
    public void consume(SendMessage sendMessage) {
        // pass
    }
}
