package ru.hselabwork.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import org.springframework.stereotype.Service;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.model.Task;
import ru.hselabwork.repository.ReminderRepository;
import ru.hselabwork.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ReminderRepository reminderRepository;

    public void deleteById(ObjectId taskId) {
        taskRepository.deleteById(taskId);
        reminderRepository.deleteAllByTaskId(taskId);
    }

    public void createTask(Task task) {
        taskRepository.save(task);
    }

    public void updateTask(Task task) {
        taskRepository.save(task);
    }

    public List<Task> getTasksFromUserIdOrderedByDeadlineAsc(ObjectId userId) {
        List<Task> tasks=  taskRepository.findAllByUserIdOrderByDeadlineAsc(userId);
        for (Task task : tasks) {
            List<Reminder> reminders = reminderRepository.findAllByTaskId(task.getId());
            task.setReminders(reminders);
        }
        return tasks;
    }

    public Optional<Task> getTaskById(ObjectId taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        optionalTask.ifPresent(task -> {
            List<Reminder> reminders = reminderRepository.findAllByTaskId(task.getId());
            task.setReminders(reminders);
        });
        return optionalTask;
    }
}
