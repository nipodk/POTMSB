package com.poweroftwo.potms_backend.balance.websockets.binance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinanceWebSocketMessageParser;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.dto.AccountUpdate;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.dto.AccountUpdateResponse;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.dto.OrderTradeResponse;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.dto.OrderTradeUpdate;
import com.poweroftwo.potms_backend.balance.services.rabbitmq.RabbitMQConfig;
import jakarta.websocket.*;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ClientEndpoint
@AllArgsConstructor
public class BinanceWebSocketEndpoint {
    private BinanceWebSocketConfig binanceWebSocketConfig;
    private RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;
    private BinanceWebSocketMessageParser binanceWebSocketMessageParser;
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connected: " + session.getId());
        session.getUserProperties().put("listenKey", binanceWebSocketConfig.getListenKey());
        session.getUserProperties().put("keyName", binanceWebSocketConfig.getKeyName());
        session.getUserProperties().put("email", binanceWebSocketConfig.getEmail());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
            final JSONObject jsonObject = new JSONObject(message);
            final String eventType = jsonObject.getString("e");
            if (eventType.equals("ORDER_TRADE_UPDATE")) {
                final OrderTradeUpdate orderTradeUpdate = binanceWebSocketMessageParser.parseOrderTradeUpdateMsg(message);
                final OrderTradeResponse orderTradeResponse = new OrderTradeResponse(orderTradeUpdate, binanceWebSocketConfig.getEmail(), binanceWebSocketConfig.getKeyName());
                try {
                    final String jsonResponse = objectMapper.writeValueAsString(orderTradeResponse);
                    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, jsonResponse);
                } catch (JsonProcessingException err) {
                    System.out.println("Couldn't convert to json orderTradeResponse");
                }
                System.out.println(orderTradeUpdate);
            } else if (eventType.equals("ACCOUNT_UPDATE")) {
                final AccountUpdate accountUpdate = binanceWebSocketMessageParser.parseAccountUpdate(message);
                final AccountUpdateResponse accountUpdateResponse = new AccountUpdateResponse(accountUpdate, binanceWebSocketConfig.getEmail(), binanceWebSocketConfig.getKeyName());
                try {
                    final String jsonResponse = objectMapper.writeValueAsString(accountUpdateResponse);
                    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, jsonResponse);
                } catch (JsonProcessingException err) {
                    System.out.println("Couldn't convert to json accountUpdateResponse");
                }
                System.out.println(accountUpdate);
            } else {
            System.out.println("Received WebSocket message: " + message);
        }
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
