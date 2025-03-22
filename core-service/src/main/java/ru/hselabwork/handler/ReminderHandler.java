package ru.hselabwork.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.ReminderService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.MessageUtils;

import java.util.Optional;

@Log4j
@Component
@RequiredArgsConstructor
public class ReminderHandler {
    private final TaskService taskService;
    private final ReminderService reminderService;
    private final ProducerService producerService;
    private final UserService userService;

    public void handle(Reminder reminder) {
        Optional<Reminder> optionalReminder = reminderService.findReminderById(reminder.getId());

        if (optionalReminder.isEmpty()) {
            log.info("Reminder with id " + reminder.getId() + " not found");
            return;
        }

        reminder = optionalReminder.get();

        if (reminder.getCancelled()) {
            log.info("Reminder with id " + reminder.getId() + " has been cancelled");
            return;
        }

        Optional<Task> optionalTask = taskService.getTaskById(reminder.getTaskId());
        if (optionalTask.isEmpty()) {
            log.info("Task with id " + reminder.getTaskId() + " not found");
            return;
        }

        Task task = optionalTask.get();
        Optional<User> optionalUser = userService.findUserById(task.getUserId());

        if (task.isCompleted() || optionalUser.isEmpty()) {
            log.info("Task with id " + reminder.getTaskId() + " has been completed");
            return;
        }

        Long chatId = optionalUser.get().getChatId();

        log.info("Task remind " + reminder.getTaskId());

        reminderService.deleteReminderById(reminder.getId());
        producerService.produceAnswer(
                MessageUtils.generateReminderMessage(chatId, task)
        );
    }
}
