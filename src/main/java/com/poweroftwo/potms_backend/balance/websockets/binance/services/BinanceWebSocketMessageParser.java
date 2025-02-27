package com.poweroftwo.potms_backend.balance.websockets.binance.services;

import com.poweroftwo.potms_backend.balance.websockets.binance.services.dto.AccountUpdate;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.dto.CoinBalance;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.dto.OrderTradeUpdate;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.dto.Position;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class BinanceWebSocketMessageParser {
    public OrderTradeUpdate parseOrderTradeUpdateMsg(String message) {
        final JSONObject fullJsonMessage = new JSONObject(message);
        final JSONObject orderTradeUpdateMsg = fullJsonMessage.getJSONObject("o");

        return mapToOrderTradeUpdate(orderTradeUpdateMsg);
    }

    public AccountUpdate parseAccountUpdate(String message) {
        final JSONObject fullJsonMessage = new JSONObject(message);
        final JSONObject accountUpdateMsg = fullJsonMessage.getJSONObject("a");
        final JSONArray walletBalances = accountUpdateMsg.getJSONArray("B");
        final JSONArray positions = accountUpdateMsg.getJSONArray("P");
        final List<CoinBalance> coinBalancesList = StreamSupport.stream(walletBalances.spliterator(), false)
                .map(this::parseCoinBalance)
                .toList();
        final List<Position> positionsList = StreamSupport.stream(positions.spliterator(), false)
                .map(this::parsePosition)
                .toList();

        return new AccountUpdate(coinBalancesList, positionsList);
    }

    private CoinBalance parseCoinBalance(Object walletBalance) {
        final JSONObject walletBalanceJson = (JSONObject) walletBalance;
        final String name = walletBalanceJson.getString("a");
        final String balance = walletBalanceJson.getString("wb");
        final String crossBalance = walletBalanceJson.getString("cw");

        return new CoinBalance(name, Float.parseFloat(balance), Float.parseFloat(crossBalance));
    }

    private Position parsePosition(Object position) {
        final JSONObject positionJson = (JSONObject) position;
        final String name = positionJson.getString("s");
        final String amount = positionJson.getString("pa");
        final String entryPrice = positionJson.getString("ep");
        final String preFeePnl = positionJson.getString("cr");
        final String unrealizedPnl = positionJson.getString("up");

        return new Position(name, Float.parseFloat(amount),  Float.parseFloat(entryPrice),
                Float.parseFloat(preFeePnl),  Float.parseFloat(unrealizedPnl));

    }

    private OrderTradeUpdate mapToOrderTradeUpdate(JSONObject orderTradeUpdateMsg) {
        final String name = orderTradeUpdateMsg.getString("s");
        final String client = orderTradeUpdateMsg.getString("c");
        final String side = orderTradeUpdateMsg.getString("S");
        final String orderType = orderTradeUpdateMsg.getString("o");
        final String orderQuantity = orderTradeUpdateMsg.getString("q");
        final String definedOrderPrice = orderTradeUpdateMsg.getString("p");
        final String actualOrderPrice = orderTradeUpdateMsg.getString("ap");
        final String positionCommission = orderTradeUpdateMsg.getString("ap");
        final String positionPnl = orderTradeUpdateMsg.getString("rp");
        final boolean isMaker = orderTradeUpdateMsg.getBoolean("m");
        final String status = orderTradeUpdateMsg.getString("X");

        return new OrderTradeUpdate(
                name,  client, side,
                orderType, Float.parseFloat(orderQuantity), Float.parseFloat(definedOrderPrice),
                Float.parseFloat(actualOrderPrice), Float.parseFloat(positionCommission), Float.parseFloat(positionPnl),
                isMaker, status
        );
    }

}
