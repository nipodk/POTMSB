package com.poweroftwo.potms_backend.balance.websockets.binance.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BinanceStreamKeyDto {
    private String apiKey;
    private String userEmail;
    private String keyName;
}