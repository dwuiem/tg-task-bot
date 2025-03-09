package ru.hselabwork.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import org.springframework.stereotype.Service;
import ru.hselabwork.model.Task;
import ru.hselabwork.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public void deleteById(ObjectId taskId) {
        taskRepository.deleteById(taskId);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task changeCompleted(Task task, boolean completed) {
        task.setCompleted(completed);
        return taskRepository.save(task);
    }

    public List<Task> getTasksFromUserId(ObjectId userId) {
        return taskRepository.findAllByUserId(userId);
    }

    public Optional<Task> getTaskById(ObjectId taskId) {
        return taskRepository.findById(taskId);
    }
}
