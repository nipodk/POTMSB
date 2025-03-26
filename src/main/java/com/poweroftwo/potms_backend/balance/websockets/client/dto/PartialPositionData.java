package com.poweroftwo.potms_backend.balance.websockets.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartialPositionData {
    private float entryPrice;
    private float positionAmt;
}