package com.poweroftwo.potms_backend.balance.websockets.client.mapper;

import com.poweroftwo.potms_backend.balance.websockets.client.dto.PositionData;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PositionDataMapper {
    public PositionData parseToPositionData(JSONObject data){
        final String symbol = data.getString("symbol");
        final float entryPrice = data.getFloat("entryPrice");
        final float positionAmt = data.getFloat("positionAmt");
        return new PositionData(symbol, entryPrice, positionAmt);
    }
}