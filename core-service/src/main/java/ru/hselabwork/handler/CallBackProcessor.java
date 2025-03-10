package ru.hselabwork.handler;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallBackProcessor {
    void process(CallbackQuery callbackQuery);
}
