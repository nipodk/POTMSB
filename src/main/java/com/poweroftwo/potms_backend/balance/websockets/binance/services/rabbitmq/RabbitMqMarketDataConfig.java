package com.poweroftwo.potms_backend.balance.websockets.binance.services.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class RabbitMqMarketDataConfig {
    public static final String EXCHANGE_NAME = "binance.exchange";
    public static final String ROUTING_KEY = "position-data";
    public static final String QUEUE_NAME = "position-data-queue";

    @Bean(name = "positionExchange")
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean(name = "positionQueue")
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean(name = "positionBinding")
    public Binding binding(@Qualifier("positionQueue") Queue queue, @Qualifier("positionExchange")DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}