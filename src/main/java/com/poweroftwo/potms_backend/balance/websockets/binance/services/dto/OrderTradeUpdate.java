package com.poweroftwo.potms_backend.balance.websockets.binance.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderTradeUpdate {
    private String name;
    private String client;
    private String side;
    private String orderType;
    private float orderQuantity;
    private float definedOrderPrice;
    private float actualOrderPrice;
    private float positionCommission;
    private float positionPnl;
    private boolean isMaker;
    private String status;
}