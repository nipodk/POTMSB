package com.poweroftwo.potms_backend.balance.services.rabbitmq;

import com.poweroftwo.potms_backend.balance.websockets.client.ClientBalanceWebSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinanceDataConsumer {
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeData(String message) throws Exception {
        ClientBalanceWebSocket.sendMessageToAllClients(message);
    }
}

