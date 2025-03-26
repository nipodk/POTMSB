package com.poweroftwo.potms_backend.balance.websockets.client;

import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinanceWebSocketConnectionService;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.SessionDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class ClientPositionDataWebSocket extends TextWebSocketHandler {
    private final BinanceWebSocketConnectionService binanceWebSocketConnectionServiceImpl;
    private static final Map<String, SessionDto> sessions = new ConcurrentHashMap<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final String clientMsg = message.getPayload();
        final JSONObject jsonObject = new JSONObject(clientMsg);
        final String clientEmail = jsonObject.getString("email");
        sessions.put(session.getId(), new SessionDto(session, clientEmail));
        binanceWebSocketConnectionServiceImpl.connectAllKeysToStream(clientEmail);
    }

    public static void sendMessageToAllClients(String message) throws Exception {
        final JSONObject jsonObject = new JSONObject(message);
        String email;
        try{
            email = jsonObject.getString("email");
        }
        catch (JSONException err) {
            return;

        }
        sessions.forEach((id, sessionData) -> {
            try {
                if (sessionData.getWebSocketSession().isOpen() && sessionData.getEmail().equals(email)) {
                    sessionData.getWebSocketSession().sendMessage(new TextMessage(message));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
