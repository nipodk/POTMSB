package com.poweroftwo.potms_backend.balance.services.rabbitmq;

import com.poweroftwo.potms_backend.balance.websockets.client.ClientPositionPnlWebSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinanceUserDataConsumer {
    @RabbitListener(queues = RabbitMQUserDataConfig.QUEUE_NAME)
    public void consumeData(String message) throws Exception {
        ClientPositionPnlWebSocket.sendMessageToAllClients(message);
    }
}

