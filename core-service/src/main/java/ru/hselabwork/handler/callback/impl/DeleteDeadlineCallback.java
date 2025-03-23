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
import ru.hselabwork.utils.MessageUtils;
import ru.hselabwork.utils.TaskUtils;

import java.util.AbstractMap;
import java.util.Optional;

import static ru.hselabwork.utils.MessageUtils.TASK_UPDATED_TEXT;
import static ru.hselabwork.utils.MessageUtils.generateSendMessage;

@Component
@RequiredArgsConstructor
public class DeleteDeadlineCallback implements CallbackProcessor {
    private final TaskService taskService;
    private final ProducerService producerService;


    @Override
    public void process(CallbackQuery callbackQuery) {
        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackUtils.parseCallbackData(callbackQuery.getData());
        Long chatId = callbackQuery.getFrom().getId();
        Optional<Task> optionalTask = taskService.getTaskById(data.getValue());

        if (optionalTask.isEmpty()) {
            producerService.produceAnswer(
                    MessageUtils.generateSendMessage(chatId, MessageUtils.TASKS_NOT_FOUND_TEXT)
            );
            return;
        }

        Task task = optionalTask.get();
        task.setDeadline(null);
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
