package ru.hselabwork.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    private ObjectId id;

    private ObjectId userId;
    private String description;
    private LocalDateTime deadline;
    private LocalDateTime created = LocalDateTime.now();
    private boolean completed;
}
