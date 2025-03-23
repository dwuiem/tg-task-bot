package ru.hselabwork.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue taskReminderQueue() {
        return QueueBuilder.durable("reminders")
                .withArgument("x-dead-letter-exchange", "reminder-exchange")
                .withArgument("x-dead-letter-routing-key", "expired-reminders")
                .build();
    }

    @Bean
    public Queue expiredReminderQueue() {
        return new Queue("expired-reminders");
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("reminder-exchange");
    }

    @Bean
    public Binding taskRemindersBinding(Queue taskReminderQueue, DirectExchange exchange) {
        return BindingBuilder.bind(taskReminderQueue).to(exchange).with("reminders");
    }

    @Bean
    public Binding expiredRemindersBinding(Queue expiredReminderQueue, DirectExchange exchange) {
        return BindingBuilder.bind(expiredReminderQueue).to(exchange).with("expired-reminders");
    }
}