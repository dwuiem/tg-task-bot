package ru.hselabwork.handler.callback.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.callback.CallbackProcessor;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.MessageUtils;

@Component
@RequiredArgsConstructor
public class DeleteTasksByDateCallback implements CallbackProcessor {
    private final ProducerService producerService;
    private final UserService userService;

    @Override
    public void process(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        User user = userService.findOrCreate(chatId);
        userService.changeState(user.getChatId(), UserState.ENTER_DELETING_DATE);
        Integer messageId = callbackQuery.getMessage().getMessageId();
        producerService.produceDelete(
                DeleteMessage.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .build()
        );
        producerService.produceAnswer(
                MessageUtils.generateSendMessage(chatId, MessageUtils.ENTER_DELETING_DATE_TEXT)
        );
    }
}
