package ru.hselabwork.configuration;

import org.springframework.amqp.core.Queue;
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
    public Queue messageQueue() {
        return new Queue("message");
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue("answer_message");
    }

    @Bean
    public Queue deleteMessageQueue() {
        return new Queue("delete_message");
    }

    @Bean
    public Queue callbackQueryQueue() {
        return new Queue("callback_query");
    }
}
