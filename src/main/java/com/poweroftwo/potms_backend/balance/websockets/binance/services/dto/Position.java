package com.poweroftwo.potms_backend.balance.websockets.binance.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Position {
    private String name;
    private float amount;
    private float entryPrice;
    private float preFeePnl;
    private float unrealizedPnl;
}
