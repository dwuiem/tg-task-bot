package ru.hselabwork.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.hselabwork.model.Reminder;
import ru.hselabwork.repository.ReminderRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private final ReminderRepository reminderRepository;

    public Reminder addReminder(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    public void deleteReminderById(ObjectId id) {
        reminderRepository.deleteById(id);
    }
    
    public Optional<Reminder> findReminderById(ObjectId id) {
        return reminderRepository.findById(id);
    }
}
