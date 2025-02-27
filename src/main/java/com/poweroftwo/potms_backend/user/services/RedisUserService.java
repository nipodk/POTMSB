package com.poweroftwo.potms_backend.user.services;

import java.util.Map;
import java.util.Set;

public interface RedisUserService {
    void storeUserSession(String email, String listenKey, String listenKeyName);
    Map<Object, Object> getUserSession(String email, String listenKeyName);
    UserSessionDto getSessionInfo(String keyName);
    Set<String> getAllUserKeys();
}
