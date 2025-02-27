package com.poweroftwo.potms_backend.balance.websockets.binance.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListenKey {
    private String name;
    private String key;
    private String email;
}
