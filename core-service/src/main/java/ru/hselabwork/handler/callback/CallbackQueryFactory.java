package ru.hselabwork.handler.callback;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.callback.impl.*;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.utils.CallbackUtils;

import java.util.AbstractMap;
import java.util.Map;

import static ru.hselabwork.utils.MessageUtils.generateSendMessage;
import static ru.hselabwork.utils.MessageUtils.NO_CALLBACK_TEXT;

@Component
@RequiredArgsConstructor
public class CallbackQueryFactory {
    private final ViewTaskCallback viewTaskCallback;
    private final CompleteTaskCallback completeTaskCallback;
    private final EditTaskDescriptionCallback editTaskDescriptionCallback;
    private final EditTaskDeadlineCallback editTaskDeadlineCallback;
    private final DeleteTaskCallback deleteTaskCallback;
    private final DeleteTasksByDateCallback deleteTasksByDateCallback;
    private final DeleteDeadlineCallback deleteDeadlineCallback;
    private final AddReminderCallback addReminderCallback;

    private final Map<String, CallbackProcessor> callbacks;

    private final ProducerService producerService;

    private static CallbackProcessor defaultCallbackProcessor;

    @PostConstruct
    public void init() {
        defaultCallbackProcessor = (callbackQuery) ->
                producerService.produceAnswer(
                        generateSendMessage(callbackQuery.getFrom().getId(), NO_CALLBACK_TEXT)
                );

        callbacks.put("view_task", viewTaskCallback);
        callbacks.put("delete_task", deleteTaskCallback);
        callbacks.put("complete_task", completeTaskCallback);
        callbacks.put("edit_description", editTaskDescriptionCallback);
        callbacks.put("add_reminder", addReminderCallback);
        callbacks.put("delete_tasks_by_date", deleteTasksByDateCallback);
        callbacks.put("edit_deadline", editTaskDeadlineCallback);
        callbacks.put("delete_deadline", deleteDeadlineCallback);
    }

    public CallbackProcessor getCallbackProcessor(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackUtils.parseCallbackData(callbackData);

        String action = data.getKey();
        return callbacks.getOrDefault(action, defaultCallbackProcessor);
    }
}
