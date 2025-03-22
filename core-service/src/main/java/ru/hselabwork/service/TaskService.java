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

    public void createTask(Task task) {
        taskRepository.save(task);
    }

    public void updateTask(Task task) {
        taskRepository.save(task);
    }

    public List<Task> getTasksFromUserIdOrderedByDeadlineAsc(ObjectId userId) {
        return taskRepository.findAllByUserIdOrderByDeadlineAsc(userId);
    }

    public Optional<Task> getTaskById(ObjectId taskId) {
        return taskRepository.findById(taskId);
    }
}
