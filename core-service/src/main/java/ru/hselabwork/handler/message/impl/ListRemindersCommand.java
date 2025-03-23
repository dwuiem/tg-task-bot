package ru.hselabwork.handler.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.message.MessageProcessor;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.ReminderService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.ReminderUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListRemindersCommand implements MessageProcessor {
    private final ProducerService producerService;
    private final UserService userService;
    private final ReminderService reminderService;

    @Override
    public void process(Message message) {
        Long chatId = message.getChatId();
        User user = userService.changeState(chatId, UserState.NONE_STATE);
        List<Reminder> reminderList = reminderService.findAllRemindersByUserId(user.getId());
        producerService.produceAnswer(
                ReminderUtils.generateReminderListMessage(chatId, reminderList)
        );
    }
}
