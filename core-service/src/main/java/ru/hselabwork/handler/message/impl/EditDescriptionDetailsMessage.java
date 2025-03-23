package ru.hselabwork.handler.message.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.message.MessageProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.TaskUtils;

import java.util.Optional;

import static ru.hselabwork.utils.MessageUtils.*;

@Log4j
@Component
@RequiredArgsConstructor
public class EditDescriptionDetailsMessage implements MessageProcessor {

    private final UserService userService;
    private final TaskService taskService;
    private final ProducerService producerService;

    @Override
    public void process(Message message) {
        User user = userService.findOrCreate(message.getFrom().getId());
        if (user.getSelectedTaskId() == null) {
            log.error("Task is not selected");
            throw new RuntimeException();
        }
        Optional<Task> optionalTask = taskService.getTaskById(user.getSelectedTaskId());
        if (optionalTask.isEmpty()) {
            producerService.produceAnswer(
                generateSendMessage(user.getChatId(), TASK_NOT_FOUND_TEXT)
            );
            return;
        }

        Task task = optionalTask.get();
        task.setDescription(message.getText());
        taskService.updateTask(task);
        userService.changeState(user.getChatId(), UserState.NONE_STATE);

        producerService.produceAnswer(
                generateSendMessage(message.getChatId(), TASK_UPDATED_TEXT)
        );

        producerService.produceAnswer(
                TaskUtils.generateTaskInfoMessage(task, user.getChatId())
        );
    }
}
