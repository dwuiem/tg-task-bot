package ru.hselabwork.handler.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.message.MessageProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.DateTimeUtils;
import ru.hselabwork.utils.MessageUtils;
import ru.hselabwork.utils.TaskUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteByDateDetailsMessage implements MessageProcessor {

    private final UserService userService;
    private final ProducerService producerService;
    private final TaskService taskService;

    @Override
    public void process(Message message) {
        Long chatId = message.getChatId();
        User user = userService.findOrCreate(chatId);
        String messageText = message.getText();
        try {
            LocalDate date = DateTimeUtils.parseDateFromText(messageText);
            taskService.deleteAllByDateAndUserId(user.getId(), date);
            producerService.produceAnswer(
                    MessageUtils.generateSendMessage(chatId, MessageUtils.TASKS_DELETED_TEXT)
            );
            userService.changeState(chatId, UserState.NONE_STATE);
            List<Task> taskList = taskService.getTasksFromUserIdOrderedByDeadlineAsc(user.getId());
            producerService.produceAnswer(
                    TaskUtils.generateTaskListMessage(taskList, chatId)
            );
        } catch (DateTimeParseException e) {
            producerService.produceAnswer(
                    MessageUtils.generateSendMessage(chatId, MessageUtils.WRONG_DATE_FORMAT)
            );
        }
    }
}
