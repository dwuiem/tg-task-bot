package ru.hselabwork.handler.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.message.MessageProcessor;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.UserService;

import static ru.hselabwork.utils.MessageUtils.ENTER_TASK_TEXT;
import static ru.hselabwork.utils.MessageUtils.generateSendMessage;

@Component
@RequiredArgsConstructor
public class AddCommand implements MessageProcessor {

    private final UserService userService;

    private final ProducerService producerService;

    @Override
    public void process(Message message) {
        Long chatId = message.getChatId();

        userService.changeState(chatId, UserState.ENTER_TASK_DETAILS);

        producerService.produceAnswer(
                generateSendMessage(chatId, ENTER_TASK_TEXT)
        );
    }
}
