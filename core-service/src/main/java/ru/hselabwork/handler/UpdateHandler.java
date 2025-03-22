package ru.hselabwork.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.callback.CallbackQueryFactory;
import ru.hselabwork.handler.message.MessageFactory;

@Component
@RequiredArgsConstructor
@Log4j
public class UpdateHandler {
    private final MessageFactory messageFactory;
    private final CallbackQueryFactory callbackQueryFactory;

    public void handleCallback(CallbackQuery callbackQuery) {
        callbackQueryFactory.getCallbackProcessor(callbackQuery).process(callbackQuery);
    }

    public void handleMessage(Message message) {
        messageFactory.getMessageProcessor(message).process(message);
    }
}
