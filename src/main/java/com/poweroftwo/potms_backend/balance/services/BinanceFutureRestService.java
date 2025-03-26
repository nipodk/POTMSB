package com.poweroftwo.potms_backend.balance.services;

import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class BinanceFutureRestService {
    final static String FUTURES_API = "https://fapi.binance.com/fapi/v1";
    final static String POSITIONS_API = "https://fapi.binance.com/fapi/v3";
    final WebClient webClient = WebClient.builder()
            .baseUrl(FUTURES_API)
            .defaultHeader("Content-Type", "application/json")
            .build();

    final WebClient webPositions = WebClient.builder()
            .baseUrl(POSITIONS_API)
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

    public ResponseEntity<Optional<String>> getFuturesServerTime() {
        Mono<String> response = webClient.get()
                .uri("/time")
                .retrieve()
                .bodyToMono(String.class);

        return new ResponseEntity<>(Optional.ofNullable(response.block()), HttpStatus.OK);
    }


    public ResponseEntity<Optional<String>> getFuturesPositions(long time, String apiKey, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        final String queryString = String.format("timestamp=%s", time);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        byte[] hash = mac.doFinal(queryString.getBytes(StandardCharsets.UTF_8));
        Mono<String> response = webPositions.get()
                .uri(String.format("/positionRisk?timestamp=%s&signature=%s", time, Hex.encodeHexString(hash)))
                .header("X-MBX-APIKEY", apiKey)
                .retrieve()
                .bodyToMono(String.class);

        return new ResponseEntity<>(Optional.ofNullable(response.block()), HttpStatus.OK);
    }
}
