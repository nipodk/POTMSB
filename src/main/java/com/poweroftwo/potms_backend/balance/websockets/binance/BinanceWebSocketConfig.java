package com.poweroftwo.potms_backend.balance.websockets.binance;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BinanceWebSocketConfig {
    private String listenKey;
    private String keyName;
    private String email;
}
