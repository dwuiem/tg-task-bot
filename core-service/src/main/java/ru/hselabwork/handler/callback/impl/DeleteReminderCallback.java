package ru.hselabwork.handler.callback.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.callback.CallbackProcessor;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.ReminderService;
import ru.hselabwork.utils.CallbackUtils;
import ru.hselabwork.utils.MessageUtils;

import java.util.AbstractMap;
import java.util.Optional;

import static ru.hselabwork.utils.MessageUtils.*;

@Component
@RequiredArgsConstructor
public class DeleteReminderCallback implements CallbackProcessor {
    private final ReminderService reminderService;
    private final ProducerService producerService;

    @Override
    public void process(CallbackQuery callbackQuery) {
        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackUtils.parseCallbackData(callbackQuery.getData());
        Long chatId = callbackQuery.getFrom().getId();
        Optional<Reminder> optionalReminder = reminderService.findReminderById(data.getValue());
        if (optionalReminder.isEmpty()) {
            producerService.produceAnswer(
                    generateSendMessage(chatId, REMINDER_NOT_FOUND_TEXT)
            );
            return;
        }
        Reminder reminder = optionalReminder.get();
        reminderService.deleteReminderById(reminder.getId());

        producerService.produceAnswer(
                MessageUtils.generateSendMessage(chatId, REMINDER_DELETED_TEXT)
        );
    }
}
