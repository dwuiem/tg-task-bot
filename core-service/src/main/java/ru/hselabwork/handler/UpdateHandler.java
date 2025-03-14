package ru.hselabwork.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.callback.CallbackQueryFactory;
import ru.hselabwork.handler.message.MessageFactory;
import ru.hselabwork.model.User;
import ru.hselabwork.service.UserService;

@Component
@RequiredArgsConstructor
@Log4j
public class UpdateHandler {
    private final MessageFactory messageFactory;
    private final CallbackQueryFactory callbackQueryFactory;
    private final UserService userService;

    public void handleCallback(CallbackQuery callbackQuery) {
        callbackQueryFactory.getCallbackProcessor(callbackQuery).process(callbackQuery);
    }

    public void handleMessage(Message message) {
        String text = message.getText();
        if (text.startsWith("/")) {
            messageFactory.getCommand(text).process(message);
        } else {
            User user = userService.findOrCreate(message.getChatId());
            messageFactory.getDetailsMessage(user.getUserState()).process(message);
        }
    }
}
