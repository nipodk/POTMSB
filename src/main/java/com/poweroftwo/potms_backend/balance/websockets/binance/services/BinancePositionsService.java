package com.poweroftwo.potms_backend.balance.websockets.binance.services;

import com.poweroftwo.potms_backend.access_key.entity.Key;
import com.poweroftwo.potms_backend.access_key.repository.AccessKeyRepository;
import com.poweroftwo.potms_backend.access_key.services.KeyHasher;
import com.poweroftwo.potms_backend.balance.services.BinanceFutureRestService;
import com.poweroftwo.potms_backend.user.repository.UserRepository;
import com.poweroftwo.potms_backend.user.repository.entities.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BinancePositionsService {
    private final BinanceFutureRestService binanceFutureRestService;
    private final UserRepository userRepository;
    private final AccessKeyRepository accessKeyRepository;
    private final KeyHasher keyHasher;

    @Transactional
    public Map<String, String> getCurrentPositions(String userEmail){
        final long time = new Date().getTime();
        final String serverTime = binanceFutureRestService.getFuturesServerTime().getBody().orElseThrow(() -> new RuntimeException("Server's time error"));
        final JSONObject serverTimeJson = new JSONObject(serverTime);
        final long parsedServerTime = serverTimeJson.getLong("serverTime");
        final long timeDif = parsedServerTime - time;
        final User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("User not found"));
        final List<Key> keys = accessKeyRepository.findAllByUser(user.getId());
        final Map<String, String> userPositions = new HashMap<>();
        keys.forEach(
                key -> {
                    try {
                        Optional<String> position = binanceFutureRestService.getFuturesPositions(
                                time + timeDif,
                                keyHasher.decrypt(key.getApiKey()),
                                keyHasher.decrypt(key.getSecreteKey())
                        ).getBody();
                        userPositions.put(key.getKeyName(), position.orElse(""));
                    }
                    catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                           IllegalBlockSizeException | BadPaddingException e) {
                        throw new RuntimeException("Can't decode the key");
                    }
                }
        );
        return userPositions;
    }
}