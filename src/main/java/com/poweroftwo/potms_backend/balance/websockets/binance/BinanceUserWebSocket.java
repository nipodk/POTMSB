package com.poweroftwo.potms_backend.balance.websockets.binance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinanceWebSocketMessageParser;
import jakarta.websocket.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;

@ClientEndpoint
@Service
@RequiredArgsConstructor
public class BinanceUserWebSocket {
    private final String WS_ENDPOINT = "wss://fstream.binance.com/ws/";
    private final ObjectMapper objectMapper;
    private final BinanceWebSocketMessageParser binanceWebSocketMessageParser;
    private final RabbitTemplate rabbitTemplate;

    public void connect(String listenKey, String keyName, String email) {
        try {
            final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            final URI uri = new URI(WS_ENDPOINT + listenKey);
            final BinanceWebSocketConfig binanceWebSocketConfig = new BinanceWebSocketConfig(listenKey, keyName, email);
            container.connectToServer(new BinanceWebSocketEndpoint(binanceWebSocketConfig,  rabbitTemplate, objectMapper, binanceWebSocketMessageParser), uri);
            System.out.println("WebSocket connected to Binance with listenKey: " + listenKey);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect to WebSocket.");
        }
    }
}
