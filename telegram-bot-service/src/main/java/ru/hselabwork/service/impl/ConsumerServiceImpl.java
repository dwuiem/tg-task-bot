package ru.hselabwork.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import ru.hselabwork.controller.UpdateController;
import ru.hselabwork.service.ConsumerService;

@Log4j
@Service
@RequiredArgsConstructor

public class ConsumerServiceImpl implements ConsumerService {
    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = "answer_message")
    public void consumeAnswerMessage(SendMessage sendMessage) {
        log.debug("Answer is received");
        updateController.setView(sendMessage);
    }

    @RabbitListener(queues = "delete_message")
    public void consumeDeleteMessage(DeleteMessage deleteMessage) {
        log.debug("Delete message is received");
        updateController.setView(deleteMessage);
    }
}
