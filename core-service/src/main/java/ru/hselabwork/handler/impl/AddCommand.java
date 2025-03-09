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
public class AddCommand implements CommandProcessor {

    private final UserService userService;

    private static final String responseText =
            """
            *Опишите задачу в следующем формате*:
            
            _<0писание>_
            _<Дата> <Время>_
            """;

    @Override
    public SendMessage process(Long chatId) {
        userService.changeState(chatId, UserState.WAITING_FOR_TASK);

        return SendMessage.builder()
                .chatId(chatId)
                .text(responseText)
                .parseMode("Markdown")
                .build();
    }
}
