package ru.hselabwork.handler.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.exception.TaskDescriptionParseException;
import ru.hselabwork.handler.message.MessageProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.TaskUtils;

import static ru.hselabwork.utils.MessageUtils.*;

@Component
@RequiredArgsConstructor
public class TaskDetailsMessage implements MessageProcessor {

    private final TaskService taskService;
    private final UserService userService;
    private final ProducerService producerService;

    @Override
    public void process(Message message) {
        User user = userService.findOrCreate(message.getChatId());
        String text = message.getText();
        try {
            Task task = TaskUtils.parseTaskFromMessage(text);
            task.setUserId(user.getId());

            taskService.createTask(task);
            userService.changeState(user.getChatId(), UserState.NONE_STATE);

            producerService.produceAnswer(
                    generateSendMessage(user.getChatId(), TASK_CREATED_TEXT)
            );
        } catch (TaskDescriptionParseException e) {
            producerService.produceAnswer(
                    generateSendMessage(user.getChatId(), WRONG_TASK_FORMAT_TEXT)
            );
        }
    }
}
