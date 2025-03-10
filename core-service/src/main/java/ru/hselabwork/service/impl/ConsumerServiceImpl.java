package ru.hselabwork.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.handler.CallbackHandler;
import ru.hselabwork.service.ConsumerService;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.handler.MessageHandler;

@Service
@Log4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;
    private final ProducerService producerService;

    @Override
    @RabbitListener(queues = "text_message_update")
    public void consumeTextMessageUpdate(Update update) {
        log.debug("Text message is received");

        SendMessage sendMessage = messageHandler.handle(update);

        producerService.produceAnswer(sendMessage);
    }

    @Override
    @RabbitListener(queues = "callback_query_update")
    public void consumeCallbackQueryUpdate(Update update) {
        log.debug("Callback query message is received");
        callbackHandler.handle(update.getCallbackQuery());
    }
}
