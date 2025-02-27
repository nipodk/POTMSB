package com.poweroftwo.potms_backend.balance.websockets.client.config;

import com.poweroftwo.potms_backend.balance.websockets.client.ClientBalanceWebSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ClientWebsocketConfig implements WebSocketConfigurer {
    private final ClientBalanceWebSocket clientBalanceWebSocket;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(clientBalanceWebSocket, "/ws/balance-stream")
                .setAllowedOrigins("*");
    }
}
