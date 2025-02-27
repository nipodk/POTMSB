package com.poweroftwo.potms_backend.balance.websockets.binance.services.dto;

import lombok.Data;

@Data
public class UserAccountMessage {
    private OrderTradeUpdate orderTradeUpdate;
    private AccountUpdate accountUpdate;
}
