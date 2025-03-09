package ru.hselabwork.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandProcessor {
    SendMessage process(Long chatId);
}
