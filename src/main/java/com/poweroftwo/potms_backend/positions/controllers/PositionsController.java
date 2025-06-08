package com.poweroftwo.potms_backend.positions.controllers;

import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinancePositionsService;
import com.poweroftwo.potms_backend.positions.dtos.UserPositions;
import com.poweroftwo.potms_backend.positions.services.UserPositionsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class PositionsController {
    private final BinancePositionsService binancePositionsService;
    private final UserPositionsMapper userPositionsMapper;
    @GetMapping("/futures/{email}")
    public UserPositions getCurrentFuturesPositions(@PathVariable String email) {
        final Map<String, String> userPositionsJson = binancePositionsService.getCurrentPositions(email);
        return userPositionsMapper.convertToUserPositions(userPositionsJson, email);
    }
}