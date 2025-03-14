package ru.hselabwork.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.service.ConsumerService;
import ru.hselabwork.handler.UpdateHandler;

@Service
@Log4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final UpdateHandler updateHandler;

    @Override
    @RabbitListener(queues = "message")
    public void consumeMessage(Message message) {
        log.debug("Message is received");
        updateHandler.handleMessage(message);
    }

    @Override
    @RabbitListener(queues = "callback_query")
    public void consumeCallbackQuery(CallbackQuery callbackQuery) {
        log.debug("Callback query message is received");
        updateHandler.handleCallback(callbackQuery);
    }
}
