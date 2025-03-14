package ru.hselabwork.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.utils.MessageUtils;

@Component
@Log4j2
@RequiredArgsConstructor
public class UpdateController {

    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final ProducerService producerService;

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
            processCallBackQuery(update.getCallbackQuery());
        } else if (message.hasText()) {
            processTextMessage(message);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void processCallBackQuery(CallbackQuery callbackQuery) {
        producerService.produceCallback("callback_query", callbackQuery);
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

    private void processTextMessage(Message message) {
        producerService.produceMessage("message", message);
    }
}
