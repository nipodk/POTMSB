package com.poweroftwo.potms_backend.balance.websockets.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PnlPositionDataMsg {
    private String email;
    private String keyName;
    private float pnl;
    private PositionData positionData;
}