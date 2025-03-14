package ru.hselabwork.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.service.ProducerService;

@Service
@Log4j
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceMessage(String rabbitQueue, Message message) {
        log.debug("Sent message to: " + rabbitQueue);
        rabbitTemplate.convertAndSend(rabbitQueue, message);
    }

    @Override
    public void produceCallback(String rabbitQueue, CallbackQuery callbackQuery) {
        log.debug("Sent callback query to: " + rabbitQueue);
        rabbitTemplate.convertAndSend(rabbitQueue, callbackQuery);
    }
}
