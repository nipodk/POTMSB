package com.poweroftwo.potms_backend.balance.websockets.binance.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AccountUpdate {
    private List<CoinBalance> coinsBalances;
    private List<Position> currentPositions;
}
