package ru.hselabwork.handler.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.hselabwork.handler.CallBackProcessor;
import ru.hselabwork.model.Task;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.utils.CallbackDataUtils;
import ru.hselabwork.utils.MessageUtils;

import java.util.AbstractMap;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ViewTaskCallback implements CallBackProcessor {

    private final ProducerService producerService;
    private final TaskService taskService;

    private static final String noTaskFound = "❗Задача <u>не найдена</u>. Возможно она была удалена";

    @Override
    public void process(CallbackQuery callbackQuery) {
        AbstractMap.SimpleEntry<String, ObjectId> data = CallbackDataUtils.parseCallbackData(callbackQuery.getData());

        Optional<Task> optionalTask = taskService.getTaskById(data.getValue());
        long chatId = callbackQuery.getFrom().getId();

        if (optionalTask.isEmpty()) {
            producerService.produceAnswer(MessageUtils.generateTaskNotFoundMessage(chatId));
        } else {
            Task task = optionalTask.get();
            producerService.produceAnswer(MessageUtils.generateTaskInfoMessage(task, chatId));
        }
    }
}
