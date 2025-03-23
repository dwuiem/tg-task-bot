package ru.hselabwork.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.ReminderHandler;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.service.ConsumerService;
import ru.hselabwork.handler.UpdateHandler;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.ReminderService;

import java.util.Optional;

@Service
@Log4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final UpdateHandler updateHandler;
    private final ReminderHandler reminderHandler;
    private final RabbitTemplate rabbitTemplate;

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

    @Override
    @RabbitListener(queues = "expired-reminders")
    public void consumeExpiredReminder(Reminder reminder) {
        log.debug("Received reminder with id: " + reminder.getId());
        reminderHandler.handle(reminder);
    }
}
