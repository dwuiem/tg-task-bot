package ru.hselabwork.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.hselabwork.handler.impl.CompleteTaskCallback;
import ru.hselabwork.handler.impl.DeleteTaskCallback;
import ru.hselabwork.handler.impl.ListCommand;
import ru.hselabwork.handler.impl.ViewTaskCallback;
import ru.hselabwork.model.Task;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.utils.CallbackDataUtils;
import ru.hselabwork.utils.TaskUtils;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CallbackHandler {
    private final CompleteTaskCallback completeTaskCallback;
    private final ViewTaskCallback viewTaskCallback;
    private final DeleteTaskCallback deleteTaskCallback;

    private final Map<String, CallBackProcessor> callbacks;

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

        callbacks.get(action).process(callbackQuery);
    }
}
