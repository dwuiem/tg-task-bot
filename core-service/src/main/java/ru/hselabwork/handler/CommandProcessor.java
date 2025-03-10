package ru.hselabwork.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CommandProcessor {
    SendMessage process(Long chatId);
}
