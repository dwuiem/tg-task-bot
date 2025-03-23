package ru.hselabwork.controller;

import jakarta.annotation.PostConstruct;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    private final UpdateController updateController;

    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    public void deleteMessage(DeleteMessage deleteMessage) {
        if (deleteMessage != null) {
            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начало"));
        listOfCommands.add(new BotCommand("/list", "Список задач"));
        listOfCommands.add(new BotCommand("/add", "Добавить задачу"));
        listOfCommands.add(new BotCommand("/list_reminders", "Просмотреть напоминания"));
        try {
            execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }
}
