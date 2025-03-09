package ru.hselabwork.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.hselabwork.handler.CommandProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.TaskUtils;

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
        String responseText;
        if (tasks.isEmpty()) {
            responseText = "На текущий момент у вас *нет* никаких задач";
        } else {
            responseText = TaskUtils.getTaskListInfo(tasks);
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(i + 1));
                button.setCallbackData("view_task:" + task.getId().toHexString());
                buttons.add(button);
            }
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(buttons));
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(responseText)
                    .replyMarkup(markup)
                    .parseMode("Markdown")
                    .build();
        }
        return SendMessage.builder()
                .chatId(chatId)
                .text(responseText)
                .parseMode("Markdown")
                .build();
    }
}
