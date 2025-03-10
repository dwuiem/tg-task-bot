package ru.hselabwork.handler.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.CallBackProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.utils.CallbackDataUtils;
import ru.hselabwork.utils.MessageUtils;

import java.util.AbstractMap;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CompleteTaskCallback implements CallBackProcessor {

    private final TaskService taskService;
    private final ProducerService producerService;

    @Override
    public void process(CallbackQuery callbackQuery) {
        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackDataUtils.parseCallbackData(callbackQuery.getData());

        var chatId = callbackQuery.getFrom().getId();

        Optional<Task> optionalTask = taskService.getTaskById(data.getValue());
        if (optionalTask.isEmpty()) {
            producerService.produceAnswer(MessageUtils.generateTaskNotFoundMessage(chatId));
        } else {
            Task task = optionalTask.get();
            taskService.changeCompleted(task, !task.isCompleted());

            Integer messageId = callbackQuery.getMessage().getMessageId();
            producerService.produceDelete(DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build()
            );

            producerService.produceAnswer(MessageUtils.generateTaskUpdatedMessage(chatId));
        }
    }
}
