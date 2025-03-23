package ru.hselabwork.handler.message.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.exception.WrongDateTimeException;
import ru.hselabwork.handler.message.MessageProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.DateTimeUtils;
import ru.hselabwork.utils.TaskUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static ru.hselabwork.utils.MessageUtils.*;

@Component
@RequiredArgsConstructor
@Log4j
public class EditDeadlineDetailsMessage implements MessageProcessor {

    private final UserService userService;
    private final TaskService taskService;
    private final ProducerService producerService;

    @Override
    public void process(Message message) {
        Long chatId = message.getChatId();
        User user = userService.findOrCreate(chatId);
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
        try {
            Task task = optionalTask.get();
            String messageText = message.getText();
            LocalDateTime newDeadline = DateTimeUtils.parseDateTimeFromText(messageText);
            task.setDeadline(newDeadline);
            taskService.updateTask(task);
            producerService.produceAnswer(
                    generateSendMessage(chatId, TASK_UPDATED_TEXT)
            );
            producerService.produceAnswer(
                    TaskUtils.generateTaskInfoMessage(task, chatId)
            );
        } catch (WrongDateTimeException e) {
            producerService.produceAnswer(
                    generateSendMessage(chatId, WRONG_DATETIME_FORMAT_TEXT)
            );
        }
    }
}
