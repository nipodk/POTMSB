package com.poweroftwo.potms_backend.balance.websockets.binance.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderTradeResponse {
    private OrderTradeUpdate orderTradeUpdate;
    private String email;
    private String keyName;
}
