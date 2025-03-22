package ru.hselabwork.handler.callback.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.callback.CallbackProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.CallbackUtils;
import ru.hselabwork.utils.MessageUtils;

import java.util.AbstractMap;
import java.util.Optional;

import static ru.hselabwork.utils.MessageUtils.generateSendMessage;
import static ru.hselabwork.utils.MessageUtils.TASK_NOT_FOUND_TEXT;

@Component
@RequiredArgsConstructor
public class EditTaskDescriptionCallback implements CallbackProcessor {

    private final TaskService taskService;
    private final ProducerService producerService;
    private final UserService userService;

    @Override
    public void process(CallbackQuery callbackQuery) {
        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackUtils.parseCallbackData(callbackQuery.getData());

        Long chatId = callbackQuery.getFrom().getId();

        Optional<Task> optionalTask = taskService.getTaskById(data.getValue());
        if (optionalTask.isEmpty()) {
            producerService.produceAnswer(
                    generateSendMessage(chatId, TASK_NOT_FOUND_TEXT)
            );
        } else {
            Task task = optionalTask.get();

            userService.changeState(chatId, UserState.AWAITING_FOR_DESCRIPTION);
            userService.selectTask(chatId, task.getId());

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
