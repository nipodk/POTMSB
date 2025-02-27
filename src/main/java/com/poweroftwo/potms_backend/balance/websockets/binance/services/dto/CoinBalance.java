package com.poweroftwo.potms_backend.balance.websockets.binance.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoinBalance {
    private String name;
    private float balance;
    private float crossBalance;
}
