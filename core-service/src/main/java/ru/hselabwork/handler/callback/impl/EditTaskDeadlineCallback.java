package ru.hselabwork.handler.callback.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.callback.CallbackProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.CallbackUtils;

import java.util.AbstractMap;
import java.util.Optional;

import static ru.hselabwork.utils.MessageUtils.*;

@Component
@RequiredArgsConstructor
public class EditTaskDeadlineCallback implements CallbackProcessor {
    private final UserService userService;
    private final TaskService taskService;
    private final ProducerService producerService;

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

            userService.changeState(chatId, UserState.ENTER_NEW_DEADLINE);
            userService.selectTask(chatId, task.getId());

            Integer messageId = callbackQuery.getMessage().getMessageId();
            producerService.produceDelete(
                    DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build()
            );

            producerService.produceAnswer(
                    generateSendMessage(chatId, ENTER_NEW_DEADLINE_TEXT)
            );
        }
    }
}
