package com.poweroftwo.potms_backend.balance.websockets.binance.services;

import com.poweroftwo.potms_backend.balance.websockets.binance.services.dto.BinanceStreamKeyDto;

public interface BinanceWebSocketConnectionService {
    void connectAllKeysToStream(String userEmail);
    void connectKeyToStream(BinanceStreamKeyDto binanceStreamKeyDto);
}
