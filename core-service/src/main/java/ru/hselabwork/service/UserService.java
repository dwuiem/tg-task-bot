package ru.hselabwork.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public User findOrCreate(Long chatId) {
        return userRepository.findByChatId(chatId).orElseGet(() -> {
            User newUser = User.builder()
                    .chatId(chatId)
                    .userState(UserState.NONE_STATE)
                    .build();
            return userRepository.save(newUser);
        });
    }

    public User selectTask(Long chatId, ObjectId taskId) {
        User user = findOrCreate(chatId);
        user.setSelectedTaskId(taskId);
        return userRepository.save(user);
    }

    public User changeState(Long chatId, UserState newState) {
        User user = findOrCreate(chatId);
        user.setUserState(newState);
        return userRepository.save(user);
    }
}
