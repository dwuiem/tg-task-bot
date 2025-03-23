package ru.hselabwork.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.model.Task;
import ru.hselabwork.repository.ReminderRepository;
import ru.hselabwork.repository.TaskRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private final ReminderRepository reminderRepository;
    private final TaskRepository taskRepository;

    public Reminder addReminder(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    public void deleteReminderById(ObjectId id) {
        reminderRepository.deleteById(id);
    }
    
    public Optional<Reminder> findReminderById(ObjectId id) {
        Optional<Reminder> optionalReminder = reminderRepository.findById(id);
        optionalReminder.ifPresent(reminder ->
            reminder.setTask(taskRepository.findById(reminder.getTaskId()).orElse(null))
        );
        return optionalReminder;
    }

    public List<Reminder> findAllRemindersByUserId(ObjectId userId) {
        List<Task> tasks = taskRepository.findAllByUserIdOrderByDeadlineAsc(userId);
        List<Reminder> reminders = new ArrayList<>();
        for (Task task : tasks) {
            List<Reminder> reminderList = reminderRepository.findAllByTaskId(task.getId());
            reminderList.forEach(reminder -> reminder.setTask(task));
            reminders.addAll(reminderList);
        }
        return reminders;
    }
}
