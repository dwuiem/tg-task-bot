package ru.hselabwork.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.handler.CommandProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.MessageUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j
public class ListCommand implements CommandProcessor {
    private final UserService userService;
    private final TaskService taskService;

    private final ProducerService producerService;

    @Override
    public void process(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userService.changeState(chatId, UserState.NONE_STATE);
        List<Task> tasks = taskService.getTasksFromUserId(user.getId());
        producerService.produceAnswer(
                MessageUtils.generateTaskListMessage(tasks, user.getChatId())
        );
    }

}
