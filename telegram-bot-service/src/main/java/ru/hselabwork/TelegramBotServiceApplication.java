package ru.hselabwork;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelegramBotServiceApplication {
    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.load();
            System.setProperty("TELEGRAM_BOT_TOKEN", dotenv.get("TELEGRAM_BOT_TOKEN"));
        } catch (Exception ignored) {}
        SpringApplication.run(TelegramBotServiceApplication.class, args);
    }
}
