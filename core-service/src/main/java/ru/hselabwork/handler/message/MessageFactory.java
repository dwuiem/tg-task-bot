package ru.hselabwork.handler.message;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hselabwork.handler.message.impl.*;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;
import ru.hselabwork.service.UserService;
import ru.hselabwork.utils.MessageUtils;

import java.util.HashMap;
import java.util.Map;

import static ru.hselabwork.utils.MessageUtils.generateSendMessage;
import static ru.hselabwork.utils.MessageUtils.NO_ANSWER_TEXT;

@Component
@RequiredArgsConstructor
public class MessageFactory {
    private final ListCommand listCommand;
    private final AddCommand addCommand;
    private final StartCommand startCommand;

    private final TaskDetailsMessage taskDetailsMessage;
    private final ReminderDetailsMessage reminderDetailsMessage;
    private final DeleteByDateDetailsMessage deleteByDateDetailsMessage;
    private final EditDescriptionDetailsMessage editDescriptionDetailsMessage;
    private final EditDeadlineDetailsMessage editDeadlineDetailsMessage;

    private final ProducerService producerService;
    private final UserService userService;

    private final Map<UserState, MessageProcessor> detailsMessages = new HashMap<>();
    private final Map<String, MessageProcessor> commandMessages = new HashMap<>();

    private static MessageProcessor defaultMessageProcessor;

    @PostConstruct
    public void init() {
        defaultMessageProcessor = (msg) ->
            producerService.produceAnswer(
                    generateSendMessage(msg.getChatId(), NO_ANSWER_TEXT)
            );

        detailsMessages.put(UserState.ENTER_TASK_DETAILS, taskDetailsMessage);
        detailsMessages.put(UserState.ENTER_NEW_DESCRIPTION, editDescriptionDetailsMessage);
        detailsMessages.put(UserState.ENTER_REMINDER_DETAILS, reminderDetailsMessage);
        detailsMessages.put(UserState.ENTER_DELETING_DATE, deleteByDateDetailsMessage);
        detailsMessages.put(UserState.ENTER_NEW_DEADLINE, editDeadlineDetailsMessage);

        commandMessages.put("/start", startCommand);
        commandMessages.put("/list", listCommand);
        commandMessages.put("/add", addCommand);

        commandMessages.put(MessageUtils.TASKS_LIST_MESSAGE, listCommand);
        commandMessages.put(MessageUtils.ADD_TASK_MESSAGE, addCommand);
    }

    public MessageProcessor getMessageProcessor(Message message) {
        String text = message.getText();
        if (commandMessages.containsKey(text)) {
            return commandMessages.getOrDefault(text, defaultMessageProcessor);
        } else {
            User user = userService.findOrCreate(message.getChatId());
            return detailsMessages.getOrDefault(user.getUserState(), defaultMessageProcessor);
        }
    }
}
