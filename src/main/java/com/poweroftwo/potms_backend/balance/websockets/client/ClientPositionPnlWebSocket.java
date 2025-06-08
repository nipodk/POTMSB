package com.poweroftwo.potms_backend.balance.websockets.client;

import com.poweroftwo.potms_backend.balance.websockets.client.dto.SessionDto;
import com.poweroftwo.potms_backend.balance.websockets.client.services.BinanceMarketDataConnection;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class ClientPositionPnlWebSocket extends TextWebSocketHandler {
    private final BinanceMarketDataConnection binanceMarketDataConnection;
    private static final Map<String, SessionDto> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Client connected PNL: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)  {
        final String clientMsg = message.getPayload();
        final JSONObject jsonObject = new JSONObject(clientMsg);
        final String clientEmail = jsonObject.getString("email");
        sessions.put(session.getId(), new SessionDto(session, clientEmail));
        binanceMarketDataConnection.connect(clientEmail);
    }

    public static void sendMessageToAllClients(String message) {
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

    public void disconect(String userEmail) {
        final SessionDto sessionDto = sessions.values().stream().filter(data -> data.getEmail().equals(userEmail))
                .findFirst()
                .orElse(null);

        if(sessionDto != null && sessionDto.getWebSocketSession().isOpen()){
            try {
                sessionDto.getWebSocketSession().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}