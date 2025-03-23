package ru.hselabwork.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private ObjectId userId;
    private String description;
    @Indexed
    private LocalDateTime deadline;
    private LocalDateTime created;
    private boolean completed;
    @DBRef // Указываем что с этим документом связаны документы с напоминанием
    private List<Reminder> reminders;
}
