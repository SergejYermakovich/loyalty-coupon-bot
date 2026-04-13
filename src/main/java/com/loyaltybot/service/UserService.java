package com.loyaltybot.service;

import com.loyaltybot.entity.User;
import com.loyaltybot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    public User getOrCreateUser(Long telegramId, String username, String firstName, 
                                 String lastName, String languageCode) {
        return userRepository.findByTelegramId(telegramId)
            .orElseGet(() -> {
                User user = new User();
                user.setTelegramId(telegramId);
                user.setUsername(username);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setLanguageCode(languageCode != null ? languageCode : "ru");
                return userRepository.save(user);
            });
    }

    public User updateUser(Long telegramId, String username, String firstName, String lastName) {
        User user = userRepository.findByTelegramId(telegramId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (username != null) user.setUsername(username);
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        
        return userRepository.save(user);
    }
}
