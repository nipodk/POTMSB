package com.poweroftwo.potms_backend.balance.websockets.binance.services;

import com.poweroftwo.potms_backend.balance.websockets.binance.BinanceMarketDataWebSocket;
import com.poweroftwo.potms_backend.balance.websockets.client.dto.PartialPositionData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class BinanceMarketDataConnectionService {
    private final BinanceMarketDataWebSocket binanceMarketDataWebSocket;
    public void connectToMarketStream(String clientEmail, Set<String> coinPairs, Map<String, Map<String, PartialPositionData>> accountPositions){
        final StringBuilder coinPairString = new StringBuilder();
        AtomicInteger itemCounter = new AtomicInteger();
        coinPairs.forEach(coin -> {
            if(coinPairs.isEmpty()){
                coinPairString.append(coin.toLowerCase()).append("@trade");
        }
            if(!(coinPairs.size() - 1 == itemCounter.get())){
                coinPairString.append(coin.toLowerCase()).append("@trade/");
            }
            else {
                coinPairString.append(coin.toLowerCase()).append("@trade");
            }
            itemCounter.getAndIncrement();
        });
        binanceMarketDataWebSocket.connect(clientEmail, coinPairString.toString(), accountPositions);
    }
}