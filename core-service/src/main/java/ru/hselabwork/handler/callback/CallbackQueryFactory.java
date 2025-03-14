package ru.hselabwork.handler.callback;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.callback.impl.CompleteTaskCallback;
import ru.hselabwork.handler.callback.impl.DeleteTaskCallback;
import ru.hselabwork.handler.callback.impl.EditTaskDescriptionCallback;
import ru.hselabwork.handler.callback.impl.ViewTaskCallback;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.utils.CallbackUtils;

import java.util.AbstractMap;
import java.util.Map;

import static ru.hselabwork.utils.MessageUtils.generateSendMessage;
import static ru.hselabwork.utils.MessageUtils.noCallbackMessage;

@Component
@RequiredArgsConstructor
public class CallbackQueryFactory {
    private final CompleteTaskCallback completeTaskCallback;
    private final ViewTaskCallback viewTaskCallback;
    private final DeleteTaskCallback deleteTaskCallback;
    private final EditTaskDescriptionCallback editTaskDescriptionCallback;

    private final Map<String, CallbackProcessor> callbacks;

    private final ProducerService producerService;

    private static CallbackProcessor defaultCallbackProcessor;

    @PostConstruct
    public void init() {
        defaultCallbackProcessor = (callbackQuery) ->
                producerService.produceAnswer(
                        generateSendMessage(callbackQuery.getFrom().getId(), noCallbackMessage)
                );

        callbacks.put("view_task", viewTaskCallback);
        callbacks.put("delete_task", deleteTaskCallback);
        callbacks.put("complete_task", completeTaskCallback);
        callbacks.put("edit_description", editTaskDescriptionCallback);
    }

    public CallbackProcessor getCallbackProcessor(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackUtils.parseCallbackData(callbackData);

        String action = data.getKey();
        return callbacks.getOrDefault(action, defaultCallbackProcessor);
    }
}
