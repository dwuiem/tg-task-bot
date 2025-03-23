package ru.hselabwork.handler.message.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.exception.ReminderParseException;
import ru.hselabwork.handler.message.MessageProcessor;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.ReminderService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.DateTimeUtils;

import java.time.*;
import java.util.Optional;

import static ru.hselabwork.utils.DateTimeUtils.getCurrentMoscowTime;
import static ru.hselabwork.utils.MessageUtils.*;

@Log4j
@Component
@RequiredArgsConstructor
public class ReminderDetailsMessage implements MessageProcessor {

    private final TaskService taskService;
    private final ReminderService reminderService;
    private final UserService userService;
    private final ProducerService producerService;

    @Override
    public void process(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();

        try {
            User user = userService.findOrCreate(chatId);

            Optional<Task> optionalTask = taskService.getTaskById(user.getSelectedTaskId());
            if (optionalTask.isEmpty()) {
                producerService.produceAnswer(
                        generateSendMessage(chatId, TASK_NOT_FOUND_TEXT)
                );
                return;
            }

            LocalDateTime reminderTime = DateTimeUtils.parseDateTimeFromText(text);

            if (reminderTime.isBefore(getCurrentMoscowTime())) {
                producerService.produceAnswer(
                        generateSendMessage(chatId, REMINDER_BEFORE_NOW)
                );
                return;
            }

            Task task = optionalTask.get();

            if (task.getDeadline() != null && reminderTime.isAfter(task.getDeadline())) {
                producerService.produceAnswer(
                        generateSendMessage(chatId, REMINDER_AFTER_DEADLINE)
                );
                return;
            }

            Reminder newReminder = Reminder.builder()
                    .reminderTime(reminderTime)
                    .taskId(task.getId())
                    .createdAt(getCurrentMoscowTime())
                    .cancelled(false)
                    .build();

            Reminder reminder = reminderService.addReminder(newReminder);
            log.debug("Reminder " + reminder.getId() + " has been added");
            userService.changeState(chatId, UserState.NONE_STATE);

            ZonedDateTime nowMoscow = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
            ZonedDateTime reminderMoscow = reminderTime.atZone(ZoneId.of("Europe/Moscow"));


            long secondsBeforeReminder = Duration.between(nowMoscow, reminderMoscow).getSeconds();
            log.debug("Second before reminder " + secondsBeforeReminder);

            producerService.produceReminder(reminder, secondsBeforeReminder);

            producerService.produceAnswer(
                    generateSendMessage(chatId, REMINDER_CREATED_TEXT)
            );

        } catch (ReminderParseException e) {
            producerService.produceAnswer(
                    generateSendMessage(chatId, WRONG_REMINDER_FORMAT_TEXT)
            );
        }
    }
}
