package ru.hselabwork.handler;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.hselabwork.handler.impl.ListCommand;
import ru.hselabwork.model.Task;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.utils.CallbackDataUtils;
import ru.hselabwork.utils.TaskUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CallbackHandler {
    private final TaskService taskService;
    private final ProducerService producerService;
    private final ListCommand listCommand;

    public SendMessage handle(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();

        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackDataUtils.parseCallbackData(callbackData);
        String action = data.getKey();

        if (action.equals("view_task")) {
            ObjectId taskId = data.getValue();
            return viewTask(callbackQuery, taskId);
        } else if (action.equals("delete_task")) {
            ObjectId taskId = data.getValue();
            return deleteTask(callbackQuery, taskId);
        } else if (action.equals("complete_task")) {
            ObjectId taskId = data.getValue();
            return completeTask(callbackQuery, taskId);
        } else {
            return SendMessage.builder()
                    .chatId(callbackQuery.getFrom().getId())
                    .text("Я ещё не умею это делать")
                    .build();
        }
    }

    private SendMessage completeTask(CallbackQuery callbackQuery, ObjectId taskId) {
        Optional<Task> taskOptional = taskService.getTaskById(taskId);
        if (taskOptional.isEmpty()) {
            return SendMessage.builder()
                    .chatId(callbackQuery.getFrom().getId())
                    .text("❗ Задача не __найдена__\\. Возможно она была удалена")
                    .parseMode("MarkdownV2")
                    .build();
        }

        Task task = taskOptional.get();
        taskService.changeCompleted(task, !task.isCompleted());

        deleteSourceMessage(callbackQuery);

        producerService.produceAnswer(SendMessage.builder()
                .chatId(callbackQuery.getFrom().getId())
                .text("✅ Задача __обновлена__ ")
                .parseMode("MarkdownV2")
                .build()
        );

        // Показываем новое
        return viewTask(callbackQuery, taskId);
    }

    private SendMessage deleteTask(CallbackQuery callbackQuery, ObjectId taskId) {
        Optional<Task> task = taskService.getTaskById(taskId);
        if (task.isEmpty()) {
            return SendMessage.builder()
                    .chatId(callbackQuery.getFrom().getId())
                    .text("❗ Задача __не найдена__\\. Возможно она была удалена")
                    .parseMode("MarkdownV2")
                    .build();
        }
        taskService.deleteById(taskId);

        deleteSourceMessage(callbackQuery);

        producerService.produceAnswer(SendMessage.builder()
                .chatId(callbackQuery.getFrom().getId())
                .text("✅ Задача успешно __удалена__")
                .parseMode("MarkdownV2")
                .build()
        );

        return listCommand.process(callbackQuery.getFrom().getId());
    }

    private SendMessage viewTask(CallbackQuery callbackQuery, ObjectId taskId) {
        Optional<Task> task = taskService.getTaskById(taskId);
        if (task.isPresent()) {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            InlineKeyboardButton deleteButton = new InlineKeyboardButton("Удалить \uD83D\uDDD1\uFE0F");
            deleteButton.setCallbackData("delete_task:" + taskId);

            InlineKeyboardButton completeButton = new InlineKeyboardButton(task.get().isCompleted() ? "Убрать ✅" : "✅");
            completeButton.setCallbackData("complete_task:" + taskId);

            InlineKeyboardButton editDescription = new InlineKeyboardButton("Изменить описание");
            editDescription.setCallbackData("edit_description:" + taskId);

            InlineKeyboardButton editDate = new InlineKeyboardButton("Изменить дату");
            editDate.setCallbackData("edit_date:" + taskId);

            InlineKeyboardButton editTime = new InlineKeyboardButton("Изменить время");
            editTime.setCallbackData("edit_time:" + taskId);

            rows.add(List.of(deleteButton, completeButton));
            rows.add(List.of(editDescription));
            rows.add(List.of(editDate));
            rows.add(List.of(editTime));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);

            return SendMessage.builder()
                    .chatId(callbackQuery.getFrom().getId())
                    .text(TaskUtils.getFullTaskInfo(task.get()))
                    .parseMode("Markdown")
                    .replyMarkup(markup)
                    .build();
        } else {
            return SendMessage.builder()
                    .chatId(callbackQuery.getFrom().getId())
                    .text("❗ Задача не найдена\\. Возможно она была удалена")
                    .parseMode("MarkdownV2")
                    .build();
        }
    }

    private void deleteSourceMessage(CallbackQuery callbackQuery) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(callbackQuery.getFrom().getId());
        deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
        producerService.produceDelete(deleteMessage);
    }
}
