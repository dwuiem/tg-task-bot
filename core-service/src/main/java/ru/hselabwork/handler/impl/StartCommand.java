package ru.hselabwork.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.handler.CommandProcessor;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.UserService;

@Component
@RequiredArgsConstructor
public class StartCommand implements CommandProcessor {
    private final UserService userService;

    private static final String welcomeMessage = "Добро пожаловать";

    @Override
    public SendMessage process(Long chatId) {
        userService.changeState(chatId, UserState.NONE_STATE);
        return SendMessage.builder()
                .chatId(chatId)
                .text(welcomeMessage)
                .build();
    }
}
