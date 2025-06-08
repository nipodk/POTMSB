package com.poweroftwo.potms_backend.balance.websockets.client.services;

import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinanceMarketDataConnectionService;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinancePositionsService;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.PartialPositionData;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.PositionData;
import com.poweroftwo.potms_backend.balance.websockets.client.mapper.PositionDataMapper;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class BinanceMarketDataConnection {
    private final BinancePositionsService binancePositionsService;
    private final PositionDataMapper positionDataMapper;
    private final BinanceMarketDataConnectionService binanceMarketDataConnectionService;
    public void connect(String clientEmail) {
        final Map<String, String> positions = binancePositionsService.getCurrentPositions(clientEmail);
        final Map<String, List<PositionData>> userPositionData = new HashMap<>();
        positions.forEach(
                (keyName, keyPosition) -> {
                    JSONArray jsonArray = new JSONArray(keyPosition);
                    final List<PositionData> positionData = new ArrayList<>();
                    jsonArray.forEach(position -> {
                        PositionData newPositionData = positionDataMapper.parseToPositionData((JSONObject) position);
                        if(newPositionData.getPositionAmt() != 0) {
                            positionData.add(newPositionData);
                        }
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

        userPositionData.forEach((key, keyPositions) -> keyPositions.forEach(positionData -> {
            Map<String, PartialPositionData> newPosition = new HashMap<>();
            PartialPositionData newPartialPosition = new PartialPositionData(positionData.getEntryPrice(),positionData.getPositionAmt(), positionData.getMarketPrice());
            newPosition.put(positionData.getSymbol(), newPartialPosition);
            if(!subAccountsPositions.containsKey(key)){
                subAccountsPositions.put(key, newPosition);
            }
            else {
                subAccountsPositions.get(key).put(positionData.getSymbol(),
                        newPartialPosition);
            }

        }));

        binanceMarketDataConnectionService.connectToMarketStream(clientEmail, uniqueSymbolsPairs, subAccountsPositions);
    }
}