package com.poweroftwo.potms_backend.balance.services.rabbitmq;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQUserDataConfig {
    public static final String EXCHANGE_NAME = "binance.exchange";
    public static final String ROUTING_KEY = "market-data";
    public static final String QUEUE_NAME = "market-data-queue";

    @Bean(name="UserDataExchange")
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean(name="UserDataQueue")
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean(name="UserDataBinding")
    public Binding binding(@Qualifier("UserDataQueue") Queue queue, @Qualifier("UserDataExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}
