package ru.hselabwork.handler.callback.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.hselabwork.handler.callback.CallbackProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.ReminderService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.utils.CallbackUtils;

import java.util.AbstractMap;
import java.util.Optional;

import static ru.hselabwork.utils.MessageUtils.*;

@Component
@RequiredArgsConstructor
public class DeleteTaskCallback implements CallbackProcessor {

    private final ProducerService producerService;
    private final TaskService taskService;
    private final ReminderService reminderService;

    @Override
    public void process(CallbackQuery callbackQuery) {
        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackUtils.parseCallbackData(callbackQuery.getData());

        Optional<Task> optionalTask = taskService.getTaskById(data.getValue());
        long chatId = callbackQuery.getFrom().getId();

        if (optionalTask.isEmpty()) {
            producerService.produceAnswer(
                    generateSendMessage(chatId, TASK_NOT_FOUND_TEXT)
            );
        } else {
            Task task = optionalTask.get();
            taskService.deleteById(task.getId());

            Integer messageId = callbackQuery.getMessage().getMessageId();
            producerService.produceDelete(
                    DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build()
            );

            producerService.produceAnswer(
                    generateSendMessage(chatId, TASK_DELETED_TEXT)
            );
        }
    }

}
