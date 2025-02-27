package com.poweroftwo.potms_backend.user.services;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class RedisUserServiceImpl implements RedisUserService {
    private final StringRedisTemplate redisTemplate;
    private static final String USER_SESSION_PREFIX = "USER_SESSION";

    public void storeUserSession(String email, String listenKey, String listenKeyName) {
        final String key = generateKeyName(email, listenKeyName);
        redisTemplate.opsForHash().put(key, "email", email);
        redisTemplate.opsForHash().put(key, "listenKey", listenKey);
        redisTemplate.opsForHash().put(key, "listenKeyName", listenKeyName);
        redisTemplate.expire(key, Duration.ofHours(24));
    }

    @Override
    public Map<Object, Object> getUserSession(String email, String listenKeyName) {
        return null;
    }

    @Override
    public UserSessionDto getSessionInfo(String keyName) {
        final String listenKey = (String) redisTemplate.opsForHash().get(keyName, "listenKey");
        final String listenKeyName = (String) redisTemplate.opsForHash().get(keyName, "listenKeyName");
        final String email = (String) redisTemplate.opsForHash().get(keyName, "email");
        return new UserSessionDto(listenKey, listenKeyName, email);
    }


//    public Map<Object, Object> getUserSession(String email, String listenKeyName) {
//        final String key = generateKeyName(email, listenKeyName);
//        return redisTemplate.opsForHash().entries(key);
//    }

    public Set<String> getAllUserKeys() {
        return redisTemplate.execute((RedisConnection connection) -> {
            final Set<String> keys = new HashSet<>();
            final ScanOptions scanoptions = ScanOptions.scanOptions()
                    .match(USER_SESSION_PREFIX + "*")
                    .count(100)
                    .build();
            try (var cursor = connection.scan(scanoptions)){
                cursor.forEachRemaining(key -> keys.add(new String(key)));
            }
            return keys;
        });
    }

    private String generateKeyName(String email, String listenKeyName){
        return USER_SESSION_PREFIX + "_" + email + "_" + listenKeyName;
    }


}
