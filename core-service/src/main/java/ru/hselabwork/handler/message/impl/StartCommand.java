package ru.hselabwork.handler.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.message.MessageProcessor;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.UserService;

@Component
@RequiredArgsConstructor
public class StartCommand implements MessageProcessor {
    private final UserService userService;
    private final ProducerService producerService;

    private static final String welcomeMessage = "Добро пожаловать";

    @Override
    public void process(Message message) {
        Long chatId = message.getChatId();
        userService.changeState(chatId, UserState.NONE_STATE);
        producerService.produceAnswer(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(welcomeMessage)
                        .build()
        );
    }
}
