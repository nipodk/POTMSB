package com.poweroftwo.potms_backend.balance.websockets.binance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinanceWebSocketMessageParser;
import com.poweroftwo.potms_backend.user.services.RedisUserService;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import lombok.RequiredArgsConstructor;
import jakarta.websocket.Session;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ClientEndpoint
@Service
@RequiredArgsConstructor
public class BinanceUserWebSocket {
    private final String WS_ENDPOINT = "wss://fstream.binance.com/ws/";
    private final ObjectMapper objectMapper;
    private final BinanceWebSocketMessageParser binanceWebSocketMessageParser;
    private final RabbitTemplate rabbitTemplate;
    private final RedisUserService redisUserService;
    private final Map<String, Session> activeSessions = new ConcurrentHashMap<>();
    public void connect(String listenKey, String keyName, String email) {
        try {
            final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            final URI uri = new URI(WS_ENDPOINT + listenKey);
            final BinanceWebSocketConfig binanceWebSocketConfig = new BinanceWebSocketConfig(listenKey, keyName, email);
            final String redisKey = redisUserService.generateKeyName(email, keyName);
            if(!redisUserService.keyExists(redisKey)){
                Session session = container.connectToServer(new BinanceUserWebSocketEndpoint(binanceWebSocketConfig,  rabbitTemplate, objectMapper, binanceWebSocketMessageParser), uri);
                activeSessions.put(keyName, session);
                redisUserService.storeUserSession(email, listenKey, keyName);
                System.out.println("WebSocket connected to Binance with listenKey: " + listenKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect to WebSocket.");
        }
    }

    public void disconnect(String email, String keyName) {
        try {
            final Session session = activeSessions.get(keyName);
            if (session != null && session.isOpen()) {
                session.close();
                activeSessions.remove(keyName);
                redisUserService.removeUserSession(redisUserService.generateKeyName(email, keyName));
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
