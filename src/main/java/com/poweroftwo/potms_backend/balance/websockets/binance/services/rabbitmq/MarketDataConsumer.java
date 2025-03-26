package com.poweroftwo.potms_backend.balance.websockets.binance.services.rabbitmq;

import com.poweroftwo.potms_backend.balance.websockets.client.ClientPositionPnlWebSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketDataConsumer {
    @RabbitListener(queues = RabbitMqMarketDataConfig.QUEUE_NAME)
    public void consumeData(String message) throws Exception {
        ClientPositionPnlWebSocket.sendMessageToAllClients(message);
    }
}