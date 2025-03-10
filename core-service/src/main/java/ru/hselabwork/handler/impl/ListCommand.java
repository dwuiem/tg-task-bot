package ru.hselabwork.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.hselabwork.handler.CommandProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j
public class ListCommand implements CommandProcessor {
    private final UserService userService;
    private final TaskService taskService;

    @Override
    public SendMessage process(Long chatId) {
        User user = userService.changeState(chatId, UserState.NONE_STATE);
        List<Task> tasks = taskService.getTasksFromUserId(user.getId());

        return MessageUtils.generateTaskListMessage(tasks, user.getChatId());
    }

}
