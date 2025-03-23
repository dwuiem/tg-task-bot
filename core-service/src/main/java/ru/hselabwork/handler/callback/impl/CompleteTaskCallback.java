package ru.hselabwork.handler.callback.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.callback.CallbackProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.utils.CallbackUtils;
import ru.hselabwork.utils.TaskUtils;

import static ru.hselabwork.utils.MessageUtils.*;

import java.util.AbstractMap;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CompleteTaskCallback implements CallbackProcessor {

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
            task.setCompleted(!task.isCompleted());
            taskService.updateTask(task);

            Integer messageId = callbackQuery.getMessage().getMessageId();

            producerService.produceDelete(
                    DeleteMessage.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .build()
            );

            producerService.produceAnswer(
                    generateSendMessage(chatId, TASK_UPDATED_TEXT)
            );

            producerService.produceAnswer(
                    TaskUtils.generateTaskInfoMessage(task, chatId)
            );
        }
    }
}
