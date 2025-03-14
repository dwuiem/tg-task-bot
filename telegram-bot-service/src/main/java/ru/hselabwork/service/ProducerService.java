package ru.hselabwork.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface ProducerService {
    void produceMessage(String rabbitQueue, Message message);
    void produceCallback(String rabbitQueue, CallbackQuery callbackQuery);
}
