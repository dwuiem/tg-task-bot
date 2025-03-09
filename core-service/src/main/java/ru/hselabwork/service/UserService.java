package ru.hselabwork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hselabwork.model.User;
import ru.hselabwork.model.UserState;
import ru.hselabwork.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findOrCreate(Long chatId) {
        return userRepository.findByChatId(chatId).orElseGet(() -> {
            User newUser = User.builder()
                    .chatId(chatId)
                    .userState(UserState.NONE_STATE)
                    .build();
            return userRepository.save(newUser);
        });
    }

    public User changeState(Long chatId, UserState newState) {
        User user = findOrCreate(chatId);
        user.setUserState(newState);
        return userRepository.save(user);
    }
}
