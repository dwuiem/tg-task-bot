package ru.hselabwork.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.model.User;
import ru.hselabwork.service.ConsumerService;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.UserService;

@Service
@Log4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final UserService userService;
    private final ProducerService producerService;

    @Override
    @RabbitListener(queues = "text_message_update")
    public void consumeTextMessageUpdate(Update update) {
        log.debug("CORE: Text message is received");

        var message = update.getMessage();
        User user = userService.findOrCreate(message.getChatId());

        var sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText("Detected CHAT ID: " + user.getChatId());

        producerService.produceAnswer(sendMessage);
    }
}
