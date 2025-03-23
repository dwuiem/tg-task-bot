package ru.hselabwork.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.hselabwork.model.Reminder;

import java.util.List;

@Repository
public interface ReminderRepository extends MongoRepository<Reminder, ObjectId> {
    void deleteAllByTaskId(ObjectId taskId);

    List<Reminder> findAllByTaskId(ObjectId id);
}
