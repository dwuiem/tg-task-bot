package ru.hselabwork.handler.message;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageProcessor {
    void process(Message message);
}
