package ru.hselabwork.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static ru.hselabwork.utils.DateTimeUtils.getCurrentMoscowTime;

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
    private LocalDateTime created = getCurrentMoscowTime();
    private boolean completed;
}
