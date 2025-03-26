package com.poweroftwo.potms_backend.balance.websockets.binance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.PartialPositionData;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BinanceMarketDataWebSocket {
    private final String WS_ENDPOINT = "wss://fstream.binance.com/stream?streams=";
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, Session> activeSessions = new ConcurrentHashMap<>();
    public void connect(String email, String coins, Map<String, Map<String, PartialPositionData>> accountPositions) {
        try {
            final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            final URI uri = new URI(WS_ENDPOINT + coins);
            if(!activeSessions.containsKey(email)) {
                Session session = container.connectToServer(new BinanceMarketDataSocketEndpoint(accountPositions, rabbitTemplate, objectMapper, email), uri);
                activeSessions.put(email, session);
                System.out.println("WebSocket connected to Binance with listenKey: " + email);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect to WebSocket.");
        }
    }

    public void disconnect(String keyName) {
        try {
            final Session session = activeSessions.get(keyName);
            if (session != null && session.isOpen()) {
                session.close();
                activeSessions.remove(keyName);
                System.out.println("WebSocket disconnected for keyName: " + keyName);
            } else {
                System.out.println("No active WebSocket session found for keyName: " + keyName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error disconnecting WebSocket for keyName: " + keyName);
        }
    }
}