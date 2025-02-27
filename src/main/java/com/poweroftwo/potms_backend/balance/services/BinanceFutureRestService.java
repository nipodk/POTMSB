package com.poweroftwo.potms_backend.balance.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class BinanceFutureRestService {
    final static String FUTURES_API = "https://fapi.binance.com/fapi/v1";
    final WebClient webClient = WebClient.builder()
            .baseUrl(FUTURES_API)
            .defaultHeader("Content-Type", "application/json")
            .build();

    public ResponseEntity<Optional<String>> getFuturesListenKey(String apiKey) {
        Mono<String> response = webClient.post()
                .uri("/listenKey")
                .header("X-MBX-APIKEY", apiKey)
                .retrieve()
                .bodyToMono(String.class);

        return new ResponseEntity<>(Optional.ofNullable(response.block()), HttpStatus.OK);
    }

    public void updateListenKey(String apiKey) {
        Mono<String> response = webClient.put()
                .uri("/listenKey")
                .header("X-MBX-APIKEY", apiKey)
                .retrieve()
                .bodyToMono(String.class);

        response.block();
    }


}
