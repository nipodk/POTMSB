package com.poweroftwo.potms_backend.balance.websockets.binance.services;

import com.poweroftwo.potms_backend.access_key.entity.Key;
import com.poweroftwo.potms_backend.access_key.repository.AccessKeyRepository;
import com.poweroftwo.potms_backend.access_key.services.KeyHasher;
import com.poweroftwo.potms_backend.balance.services.BinanceFutureRestService;
import com.poweroftwo.potms_backend.balance.websockets.binance.BinanceUserWebSocket;
import com.poweroftwo.potms_backend.user.repository.UserRepository;
import com.poweroftwo.potms_backend.user.repository.entities.User;
import com.poweroftwo.potms_backend.user.services.RedisUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BinanceWebSocketConnectionService {
    private final BinanceUserWebSocket binanceUserWebSocket;
    private final BinanceFutureRestService binanceFutureRestService;
    private final AccessKeyRepository accessKeyRepository;
    private final UserRepository userRepository;
    private final KeyHasher keyHasher;
    private final RedisUserService redisUserService;
    @Transactional
    public void connectToStream(String userEmail) {
        final User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("User not found"));
        final List<Key> keys = accessKeyRepository.findAllByUser(user.getId());
        final Map<String, String> listenKeys = new HashMap<>();
                keys.forEach(key -> {
                    try {
                        final String apiKey = keyHasher.decrypt(key.getApiKey());
                        final String listenKey = binanceFutureRestService.getFuturesListenKey(apiKey).getBody().orElseThrow(
                                () -> new RuntimeException("Listen key isn't returned"));
                        final JSONObject jsonObject = new JSONObject(listenKey);
                        final String parsedListenKey = jsonObject.getString("listenKey");
                        redisUserService.storeUserSession(userEmail, parsedListenKey, key.getKeyName());
                        listenKeys.put(parsedListenKey, key.getKeyName());
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                             IllegalBlockSizeException | BadPaddingException e) {
                        throw new RuntimeException("Can't decode the key");
                    }
        });
        listenKeys.forEach((key, value) -> binanceUserWebSocket.connect(key, value, userEmail));
    }
}
