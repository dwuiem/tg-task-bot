package ru.hselabwork.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.service.ConsumerService;
import ru.hselabwork.service.ProducerService;

@Service
@Log4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final ProducerService producerService;

    @Override
    @RabbitListener(queues = "text_message_update")
    public void consumeTextMessageUpdate(Update update) {
        log.debug("CORE: Text message is received");
        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText("Hi there");
        producerService.produceAnswer(sendMessage);
    }
}
