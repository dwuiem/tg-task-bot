package ru.hselabwork.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandProcessor {
    void process(Update update);
}
