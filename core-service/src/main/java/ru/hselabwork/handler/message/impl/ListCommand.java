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

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j
public class ListCommand implements MessageProcessor {
    private final UserService userService;
    private final TaskService taskService;

    private final ProducerService producerService;

    @Override
    public void process(Message message) {
        Long chatId = message.getChatId();
        User user = userService.changeState(chatId, UserState.NONE_STATE);
        List<Task> tasks = taskService.getTasksFromUserIdOrderedByDeadlineAsc(user.getId());
        producerService.produceAnswer(
                TaskUtils.generateTaskListMessage(tasks, user.getChatId())
        );
    }

}
