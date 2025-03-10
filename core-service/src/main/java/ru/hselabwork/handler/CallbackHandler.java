package ru.hselabwork.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.impl.CompleteTaskCallback;
import ru.hselabwork.handler.impl.DeleteTaskCallback;
import ru.hselabwork.handler.impl.ViewTaskCallback;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.utils.CallbackDataUtils;


import java.util.*;

@Component
@RequiredArgsConstructor
public class CallbackHandler {
    private final CompleteTaskCallback completeTaskCallback;
    private final ViewTaskCallback viewTaskCallback;
    private final DeleteTaskCallback deleteTaskCallback;

    private final Map<String, CallBackProcessor> callbacks;
    private final ProducerService producerService;

    @PostConstruct
    public void init() {
        callbacks.put("view_task", viewTaskCallback);
        callbacks.put("delete_task", deleteTaskCallback);
        callbacks.put("complete_task", completeTaskCallback);
    }


    public void handle(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();

        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackDataUtils.parseCallbackData(callbackData);
        String action = data.getKey();
        if (!callbacks.containsKey(action)) {
            // TODO
            producerService.produceAnswer(SendMessage.builder()
                    .chatId(callbackQuery.getFrom().getId())
                    .text("Я ещё не умею это обрабатывать")
                    .build());
        } else {
            callbacks.get(action).process(callbackQuery);
        }
    }
}
