package com.poweroftwo.potms_backend.balance.services;

import com.poweroftwo.potms_backend.balance.websockets.binance.BinanceUserWebSocket;
import com.poweroftwo.potms_backend.user.services.RedisUserService;
import com.poweroftwo.potms_backend.user.services.UserSessionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BinanceUserDataSocketUpdater {
    private final BinanceFutureRestService binanceFutureRestService;
    private final BinanceUserWebSocket binanceUserWebSocket;
    private final RedisUserService redisUserService;
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void updateKey(){
        final Set<String> keys = redisUserService.getAllUserKeys();
        final List<UserSessionDto> userSessionDtoList = keys.stream().map(redisUserService::getSessionInfo).toList();
        userSessionDtoList.forEach(userSessionDto -> binanceFutureRestService.updateListenKey(userSessionDto.getListenKey()));
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void reconnectToDataSocket(){
        final Set<String> keys = redisUserService.getAllUserKeys();
        final List<UserSessionDto> userSessionDtoList = keys.stream().map(redisUserService::getSessionInfo).toList();
        userSessionDtoList.forEach(userSessionDto -> {
            final String renewedListenKey = binanceFutureRestService.getFuturesListenKey(userSessionDto.getListenKey()).getBody().orElseThrow(() -> new RuntimeException("Renewed listen key isn't returned"));
            binanceUserWebSocket.connect(renewedListenKey, userSessionDto.getListenKeyName(), userSessionDto.getEmail());
        });
    }
}
