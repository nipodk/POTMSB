package com.poweroftwo.potms_backend.user.services;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ScanOptions;
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

    public void storeUserSession(String email, String listenKey, String keyName) {
        final String key = generateKeyName(email, keyName);
        redisTemplate.opsForHash().put(key, "email", email);
        redisTemplate.opsForHash().put(key, "listenKey", listenKey);
        redisTemplate.opsForHash().put(key, "keyName", keyName);
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

    public boolean keyExists(String key) {
        return redisTemplate.hasKey(key);
    }

    public String generateKeyName(String email, String keyName){
        return USER_SESSION_PREFIX + "_" + email + "_" + keyName;
    }

    @Override
    public boolean removeUserSession(String keyName) {
        return redisTemplate.delete(keyName);
    }

    @PostConstruct
    public void clearRedisOnStartup() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        System.out.println("Redis database cleared on application startup.");
    }


}
