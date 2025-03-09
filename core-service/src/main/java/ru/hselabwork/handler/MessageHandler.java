package ru.hselabwork.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hselabwork.handler.impl.AddCommand;
import ru.hselabwork.handler.impl.ListCommand;
import ru.hselabwork.handler.impl.StartCommand;
import ru.hselabwork.model.Task;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.TaskService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.TaskUtils;

import java.time.format.DateTimeParseException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessageHandler {
    private final UserService userService;
    private final TaskService taskService;

    private final Map<String, CommandProcessor> commands;

    private final StartCommand startCommand;
    private final ListCommand listCommand;
    private final AddCommand addCommand;

    private static final String commandNotFound = "Такой команды не существует";
    private static final String noResponseMessage = "Нет такого варианта ответа";

    @PostConstruct
    public void init() {
        commands.put("/start", startCommand);
        commands.put("/list", listCommand);
        commands.put("/add", addCommand);
    }

    public SendMessage handle(Update update) {
        String text = update.getMessage().getText();
        if (text.startsWith("/")) {
            return handleCommand(update);
        } else {
            return handleText(update);
        }
    }

    private SendMessage handleCommand(Update update) {
        String command = update.getMessage().getText();
        return commands.getOrDefault(command, (id) -> SendMessage.builder()
                        .chatId(id)
                        .text(commandNotFound)
                        .build())
                .process(update.getMessage().getChatId());
    }

    private SendMessage handleText(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userService.findOrCreate(chatId);
        switch (user.getUserState()) {
            case WAITING_FOR_TASK:
                String text = update.getMessage().getText();
                try {
                    Task task = TaskUtils.parseTaskFromMessage(text);
                    if (task == null) {
                        return SendMessage.builder()
                                .chatId(update.getMessage().getChatId())
                                .text("❗ Неверный формат задачи. Попробуйте заново")
                                .build();
                    }
                    task.setUserId(user.getId());

                    taskService.createTask(task);
                    userService.changeState(chatId, UserState.NONE_STATE);

                    return SendMessage.builder()
                            .chatId(update.getMessage().getChatId())
                            .text("✅ Задача успешно создана")
                            .build();
                } catch (DateTimeParseException e) {
                    return SendMessage.builder()
                            .chatId(update.getMessage().getChatId())
                            .text("❗ Неверный формат даты / времени")
                            .build();
                }
            default:
                return SendMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .text(noResponseMessage)
                        .build();
        }
    }
}
