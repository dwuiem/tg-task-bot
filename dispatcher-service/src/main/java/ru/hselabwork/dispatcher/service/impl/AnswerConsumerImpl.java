package ru.hselabwork.dispatcher.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hselabwork.dispatcher.controller.UpdateController;
import ru.hselabwork.dispatcher.service.AnswerConsumer;

@Service
@RequiredArgsConstructor

public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = "answer_message")
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}
