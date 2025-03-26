package com.poweroftwo.potms_backend.access_key.services;

import com.poweroftwo.potms_backend.access_key.controllers.dtos.*;
import com.poweroftwo.potms_backend.access_key.entity.Key;
import com.poweroftwo.potms_backend.access_key.mapper.AccessKeyMapper;
import com.poweroftwo.potms_backend.access_key.repository.AccessKeyRepository;
import com.poweroftwo.potms_backend.balance.services.BinanceFutureRestService;
import com.poweroftwo.potms_backend.balance.websockets.binance.BinanceUserWebSocket;
import com.poweroftwo.potms_backend.balance.websockets.binance.services.BinanceWebSocketConnectionService;
import com.poweroftwo.potms_backend.user.repository.UserRepository;
import com.poweroftwo.potms_backend.user.repository.entities.User;
import com.poweroftwo.potms_backend.user.services.RedisUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessKeyServiceImpl implements AccessKeyService {
    private final AccessKeyRepository accessKeyRepository;
    private final AccessKeyMapper accessKeyMapper;
    private final UserRepository userRepository;
    private final KeyHasherImpl keyHasher;
    private final BinanceWebSocketConnectionService binanceWebSocketConnectionService;
    private final BinanceFutureRestService binanceFutureRestService;
    private final RedisUserService redisUserService;
    private final BinanceUserWebSocket binanceUserWebSocket;
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public KeyCreateResponse createKey(KeyCreateRequest keyCreateRequest) throws Exception {
        final Optional<User> user = userRepository.findByEmail(keyCreateRequest.getUserEmail());
        final Integer keyNameOccurs = accessKeyRepository.countByName(keyCreateRequest.getKeyName());
        if(user.isEmpty()){
            throw new RuntimeException("User not found. Can't create key");
        }
        if(keyNameOccurs >= 1){
            throw new RuntimeException("Key with this name already exists");
        }
        final Key key = accessKeyMapper.createDtoToEntity(keyCreateRequest);
        key.setApiKey(keyHasher.encrypt(keyCreateRequest.getApiKey()));
        key.setSecreteKey(keyHasher.encrypt(keyCreateRequest.getSecreteKey()));
        key.setUserId(user.get().getId());
        final boolean keyIsValid = checkKey(keyCreateRequest.getApiKey());
        if(!keyIsValid) {
            throw new Exception("Key not valid");
        }
        final Key keyCreateResponse = accessKeyRepository.save(key);
        return accessKeyMapper.entityToCreateDtoResponse(keyCreateResponse);
    }

    @Override
    @Transactional
    public KeyGetRequest getAllKeys(String userEmail) {
        final User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("This user doesn't exists"));
        final List<Key> fetchedUserKeys = accessKeyRepository.findAllByUser(user.getId());
        final List<KeyDto> userKeysInfo = fetchedUserKeys.stream().map(key -> {
            final KeyDto keyDto = accessKeyMapper.entityToGetDtoResponse(key);
            try {
                keyDto.setApiKey(keyHasher.decrypt(key.getApiKey()));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("Can't decrypt the api key");
            }
            try {
                keyDto.setApiKey(keyHasher.decrypt(key.getSecreteKey()));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("Can't decrypt the api key");
            }
            return keyDto;
        }).toList();
        return new KeyGetRequest(userKeysInfo, userEmail);
    }

    @Override
    @Transactional
    public KeyUpdateResponse updateKey(KeyUpdateRequest keyUpdateRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        final User user = userRepository.findByEmail(keyUpdateRequest.getUserEmail()).orElseThrow(() -> new EntityNotFoundException("This user doesn't exists"));
        final Key userKey = accessKeyRepository.findUserKey(keyUpdateRequest.getKeyName(), user.getId()).orElseThrow(() -> new EntityNotFoundException("Key doesn't exist"));
        userKey.setKeyName(keyUpdateRequest.getKeyDto().getKeyName());
        userKey.setApiKey(keyHasher.encrypt(keyUpdateRequest.getKeyDto().getApiKey()));
        userKey.setUserId(user.getId());
        accessKeyRepository.save(userKey);
        return new KeyUpdateResponse(keyUpdateRequest.getKeyDto(), true);
    }

    @Transactional
    public KeyRemoveResponse deleteKey(String userEmail, String keyName) {
        final User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("This user doesn't exists"));
        final Key userKey = accessKeyRepository.findUserKey(keyName, user.getId()).orElseThrow(() -> new EntityNotFoundException("Key doesn't exist"));
        accessKeyRepository.delete(userKey);
        redisUserService.removeUserSession(redisUserService.generateKeyName(userEmail, keyName));
        binanceUserWebSocket.disconnect(userEmail, keyName);
        return new KeyRemoveResponse(keyName, true);
    }

    private boolean checkKey(String apiKey) {
        try {
            final ResponseEntity<Optional<String>> listenKey = binanceFutureRestService.getFuturesListenKey(apiKey);
            return true;
        }
        catch (Exception err) {
            return false;
        }
    }
}
