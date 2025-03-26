package com.poweroftwo.potms_backend.balance.websockets.binance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.rabbitmq.RabbitMqMarketDataConfig;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.PartialPositionData;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.PnlPositionDataMsg;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.PositionData;
import jakarta.websocket.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

@ClientEndpoint
@RequiredArgsConstructor
public class BinanceMarketDataSocketEndpoint {
    private final Map<String, Map<String, PartialPositionData>> userPositions;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String clientEmail;
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        final JSONObject jsonObject = new JSONObject(message);
        final JSONObject priceData = jsonObject.getJSONObject("data");
        final float coinPrice = priceData.getFloat("p");
        final String coinPair = priceData.getString("s");
        userPositions.forEach((key, positionData) -> {
            if(positionData.containsKey(coinPair)){
                PartialPositionData partialPositionData = positionData.get(coinPair);
                float PNL = (coinPrice - partialPositionData.getEntryPrice()) * partialPositionData.getPositionAmt();
                PnlPositionDataMsg pnlPositionData = new PnlPositionDataMsg(clientEmail, key, PNL, new PositionData(coinPair, partialPositionData.getEntryPrice(), partialPositionData.getPositionAmt()));
                try {
                    final String jsonResponse = objectMapper.writeValueAsString(pnlPositionData);
                    rabbitTemplate.convertAndSend(RabbitMqMarketDataConfig.EXCHANGE_NAME, RabbitMqMarketDataConfig.ROUTING_KEY, jsonResponse);
                } catch (JsonProcessingException err) {
                    System.out.println("Couldn't convert to json orderTradeResponse");
                }
                System.out.println("Received WebSocket message: " + coinPair + " " + pnlPositionData);
            }
        });
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket connection closed: " + reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }
}