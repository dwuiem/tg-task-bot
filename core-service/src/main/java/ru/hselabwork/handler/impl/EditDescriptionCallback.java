package ru.hselabwork.handler.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.CallBackProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.CallbackDataUtils;
import ru.hselabwork.utils.MessageUtils;

import java.util.AbstractMap;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EditDescriptionCallback implements CallBackProcessor {

    private final TaskService taskService;
    private final ProducerService producerService;
    private final UserService userService;

    @Override
    public void process(CallbackQuery callbackQuery) {
        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackDataUtils.parseCallbackData(callbackQuery.getData());

        var chatId = callbackQuery.getFrom().getId();

        Optional<Task> optionalTask = taskService.getTaskById(data.getValue());
        if (optionalTask.isEmpty()) {
            producerService.produceAnswer(MessageUtils.generateTaskNotFoundMessage(chatId));
        } else {

            // TODO: Create new state
            User user = userService.changeState(chatId, UserState.NONE_STATE);

            Integer messageId = callbackQuery.getMessage().getMessageId();
            producerService.produceDelete(
                    DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build()
            );

            producerService.produceAnswer(MessageUtils.generateEnterDescriptionMessage(chatId));
        }
    }
}
