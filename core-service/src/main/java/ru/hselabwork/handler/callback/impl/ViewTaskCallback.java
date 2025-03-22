package ru.hselabwork.handler.callback.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewTaskCallback implements CallbackProcessor {

    private final ProducerService producerService;
    private final TaskService taskService;
    private final UserService userService;

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
            userService.changeState(chatId, UserState.NONE_STATE);
            userService.selectTask(chatId, task.getId());
            log.debug("User {} selected task: {}", callbackQuery.getFrom().getUserName(), task.getDescription());
            producerService.produceAnswer(MessageUtils.generateTaskInfoMessage(task, chatId));
        }
    }
}
