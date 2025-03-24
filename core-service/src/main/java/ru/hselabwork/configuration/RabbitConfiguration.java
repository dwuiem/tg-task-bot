package ru.hselabwork.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfiguration {
    // Конвертер для сообщений
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue taskReminderQueue() {
        return new Queue("reminders", true);
    }

    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("reminder-exchange", "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding taskRemindersBinding() {
        return BindingBuilder.bind(taskReminderQueue())
                .to(delayedExchange())
                .with("routing-key")
                .noargs();
    }
}
