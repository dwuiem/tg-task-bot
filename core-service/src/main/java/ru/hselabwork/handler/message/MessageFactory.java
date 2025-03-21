package ru.hselabwork.handler.message;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hselabwork.handler.message.impl.*;
import ru.hselabwork.model.UserState;
import ru.hselabwork.service.ProducerService;

import java.util.HashMap;
import java.util.Map;

import static ru.hselabwork.utils.MessageUtils.generateSendMessage;
import static ru.hselabwork.utils.MessageUtils.noAnswerMessage;

@Component
@RequiredArgsConstructor
public class MessageFactory {
    private final ListCommand listCommand;
    private final AddCommand addCommand;
    private final StartCommand startCommand;

    private final TaskDetailsMessage taskDetailsMessage;
    private final EditDescriptionDetailsMessage editDescriptionDetailsMessage;
    private final ReminderDetailsMessage reminderDetailsMessage;


    private final ProducerService producerService;

    private final Map<UserState, MessageProcessor> detailsMessages = new HashMap<>();;
    private final Map<String, MessageProcessor> commandMessages = new HashMap<>();;

    private static MessageProcessor defaultMessageProcessor;

    @PostConstruct
    public void init() {
        defaultMessageProcessor = (msg) ->
            producerService.produceAnswer(
                    generateSendMessage(msg.getChatId(), noAnswerMessage)
            );

        detailsMessages.put(UserState.AWAITING_FOR_TASK, taskDetailsMessage);
        detailsMessages.put(UserState.AWAITING_FOR_DESCRIPTION, editDescriptionDetailsMessage);
        detailsMessages.put(UserState.AWAITING_FOR_REMINDER, reminderDetailsMessage);

        commandMessages.put("/start", startCommand);
        commandMessages.put("/list", listCommand);
        commandMessages.put("/add", addCommand);
    }

    public MessageProcessor getCommand(String command) {
        return commandMessages.getOrDefault(command, defaultMessageProcessor);
    }

    public MessageProcessor getDetailsMessage(UserState userState) {
        return detailsMessages.getOrDefault(userState, defaultMessageProcessor);
    }
}
