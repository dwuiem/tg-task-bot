package ru.hselabwork.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.handler.CommandProcessor;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.UserService;

@Component
@RequiredArgsConstructor
public class StartCommand implements CommandProcessor {
    private final UserService userService;
    private final ProducerService producerService;

    private static final String welcomeMessage = "Добро пожаловать";

    @Override
    public void process(Update update) {
        Long chatId = update.getMessage().getChatId();
        userService.changeState(chatId, UserState.NONE_STATE);
        producerService.produceAnswer(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(welcomeMessage)
                        .build()
        );
    }
}
