package ru.hselabwork.dispatcher.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.dispatcher.service.UpdateProducer;
import ru.hselabwork.dispatcher.utils.MessageUtils;

@Component
@Log4j2
@RequiredArgsConstructor
public class UpdateController {

    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Update is null");
            return;
        }
        distributeMessageByType(update);
    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if (update.hasCallbackQuery()) {
            processCallBackQuery(update);
        } else if (message.hasText()) {
            processTextMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void processCallBackQuery(Update update) {
        updateProducer.produce("callback_query_update", update);
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    public void setView(DeleteMessage deleteMessage) {
        telegramBot.deleteMessage(deleteMessage);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce("text_message_update", update);
    }
}
