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
public class AddCommand implements CommandProcessor {

    private final UserService userService;

    private static final String responseText =
            """
            <b>Опишите задачу в следующем формате</b>:
            
            <i>[0писание задачи]</i>
            <i>[Дата 01.01.1111] [Время 11:11]</i>
            """;

    private final ProducerService producerService;

    @Override
    public void process(Update update) {
        Long chatId = update.getMessage().getChatId();

        userService.changeState(chatId, UserState.WAITING_FOR_TASK);

        producerService.produceAnswer(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(responseText)
                        .parseMode("HTML")
                        .build()
        );
    }
}
