package ru.hselabwork.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.hselabwork.model.Reminder;

@Repository
public interface ReminderRepository extends MongoRepository<Reminder, ObjectId> { }
