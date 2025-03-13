package com.poweroftwo.potms_backend.balance.websockets.binance.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountUpdateResponse {
    private AccountUpdate accountUpdate;
    private String email;
    private String keyName;
    private String eventName;
}
