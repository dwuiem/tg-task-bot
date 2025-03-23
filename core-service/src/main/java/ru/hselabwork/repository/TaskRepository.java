package ru.hselabwork.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.hselabwork.model.Task;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, ObjectId> {
    List<Task> findAllByUserIdOrderByDeadlineAsc(ObjectId userId);
    List<Task> findAllByUserIdAndDeadlineBetween(ObjectId userId, LocalDateTime start, LocalDateTime end);
}
