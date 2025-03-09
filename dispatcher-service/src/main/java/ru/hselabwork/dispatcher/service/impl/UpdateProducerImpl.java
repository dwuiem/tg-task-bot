package ru.hselabwork.dispatcher.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.dispatcher.service.UpdateProducer;

@Service
@Log4j
@RequiredArgsConstructor
public class UpdateProducerImpl implements UpdateProducer {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug("Sent message to: " + rabbitQueue);
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
