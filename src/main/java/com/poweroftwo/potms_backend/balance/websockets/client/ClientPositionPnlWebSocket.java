package com.poweroftwo.potms_backend.balance.websockets.client;

import com.poweroftwo.potms_backend.balance.websockets.binance.BinanceMarketDataWebSocket;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinanceMarketDataConnectionService;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinancePositionsService;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.PartialPositionData;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.PositionData;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.SessionDto;
import com.poweroftwo.potms_backend.balance.websockets.client.mapper.PositionDataMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class ClientPositionPnlWebSocket extends TextWebSocketHandler {
    private final BinanceMarketDataWebSocket binanceMarketDataWebSocket;
    private final BinancePositionsService binancePositionsService;
    private final PositionDataMapper positionDataMapper;
    private final BinanceMarketDataConnectionService binanceMarketDataConnectionService;
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
        final Map<String, String> positions = binancePositionsService.getCurrentPositions(clientEmail);
        final Map<String, List<PositionData>> userPositionData = new HashMap<>();
        positions.forEach(
                (keyName, keyPosition) -> {
                    JSONArray jsonArray = new JSONArray(keyPosition);
                    final List<PositionData> positionData = new ArrayList<>();
                    jsonArray.forEach(position -> {
                        positionData.add(positionDataMapper.parseToPositionData((JSONObject) position));
                    });
                    userPositionData.put(keyName, positionData);
                }
        );
        final Set<String> uniqueSymbolsPairs = new HashSet<>();
        userPositionData.values().forEach(
                positionList -> positionList.forEach(
                        item -> uniqueSymbolsPairs.add(item.getSymbol())
                )
                );

        final Map<String, Map<String, PartialPositionData>> subAccountsPositions = new HashMap<>();

        userPositionData.forEach((key, keyPositions) -> {
                    keyPositions.forEach(positionData -> {
                        Map<String, PartialPositionData> newPosition = new HashMap<>();
                        PartialPositionData newPartialPosition = new PartialPositionData(positionData.getEntryPrice(),positionData.getPositionAmt());
                        newPosition.put(positionData.getSymbol(), newPartialPosition);
                        if(!subAccountsPositions.containsKey(key)){
                            subAccountsPositions.put(key, newPosition);
                        }
                        else {
                            subAccountsPositions.get(key).put(positionData.getSymbol(),
                                    newPartialPosition);
                        }

                    });
                });

        System.out.println(subAccountsPositions);
        binanceMarketDataConnectionService.connectToMarketStream(clientEmail, uniqueSymbolsPairs, subAccountsPositions);
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