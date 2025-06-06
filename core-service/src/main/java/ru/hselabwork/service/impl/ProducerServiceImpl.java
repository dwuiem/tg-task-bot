package ru.hselabwork.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.service.ProducerService;

@Service
@RequiredArgsConstructor
@Log4j
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        log.debug("Sent answer");
        rabbitTemplate.convertAndSend("answer_message", sendMessage);
    }

    @Override
    public void produceDelete(DeleteMessage deleteMessage) {
        log.debug("Sent delete message");
        rabbitTemplate.convertAndSend("delete_message", deleteMessage);
    }

    @Override
    public void produceReminder(Reminder reminder, long reminderTimeInSeconds) {
        log.debug("Sent reminder");
        long delay = reminderTimeInSeconds * 1000L; // преобразуем в миллисекунды
        log.debug("Delay: " + delay);

        rabbitTemplate.convertAndSend(
                "reminder-exchange", // обменник
                "routing-key", // ключ маршрутизации
                reminder, // тело сообщения
                message -> {
                    // Устанавливаем заголовок x-delay для отложенного сообщения
                    message.getMessageProperties().setHeader("x-delay", delay);
                    return message;
                }
        );
    }
}

