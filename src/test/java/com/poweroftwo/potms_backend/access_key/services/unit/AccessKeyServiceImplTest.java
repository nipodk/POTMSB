package com.poweroftwo.potms_backend.access_key.services.unit;

import com.poweroftwo.potms_backend.access_key.controllers.dtos.*;
import com.poweroftwo.potms_backend.access_key.entity.Key;
import com.poweroftwo.potms_backend.access_key.mapper.AccessKeyMapper;
import com.poweroftwo.potms_backend.access_key.repository.AccessKeyRepository;
import com.poweroftwo.potms_backend.access_key.services.AccessKeyServiceImpl;
import com.poweroftwo.potms_backend.access_key.services.KeyHasherImpl;
import com.poweroftwo.potms_backend.balance.services.BinanceFutureRestService;
import com.poweroftwo.potms_backend.balance.websockets.binance.BinanceUserWebSocket;
import com.poweroftwo.potms_backend.user.repository.UserRepository;
import com.poweroftwo.potms_backend.user.repository.entities.Role;
import com.poweroftwo.potms_backend.user.repository.entities.User;
import com.poweroftwo.potms_backend.user.services.RedisUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AccessKeyServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccessKeyRepository accessKeyRepository;
    @Mock
    private KeyHasherImpl keyHasher;
    @Mock
    private BinanceFutureRestService binanceFutureRestService;
    @Mock
    private RedisUserService redisUserService;
    @Mock
    private BinanceUserWebSocket binanceUserWebSocket;
    @Mock
    private WebClient webClient;
    @Mock
    private AccessKeyMapper accessKeyMapper;
    @InjectMocks
    private AccessKeyServiceImpl accessKeyService;

    @BeforeEach
    public void init() {
        final Key userKey = new Key(
                1,
                "keyName",
                "apiKey",
                "secreteKey",
                new Date(),
                1
        );
    }


    @Test
    void createKey() throws Exception {
        final KeyCreateRequest keyCreateRequest = new KeyCreateRequest(
                "keyName",
                "apiKey",
                "secreteKey",
                new Date(),
                "mail@gmail.com"
        );

        final Key userKey = new Key(
        1,
        "keyName",
        "apiKey",
        "secreteKey",
        new Date(),
        1
        );

        final KeyCreateResponse keyCreateResponse = new KeyCreateResponse("keyName", new Date());


        final User user = new User(
                1,
        "firstName",
        "lastName",
        "mail@gmail.com",
        "password",
        Role.USER,
        List.of()
        );

        Mockito.when(userRepository.findByEmail(keyCreateRequest.getUserEmail())).thenReturn(Optional.of(user));
        Mockito.when(accessKeyRepository.countByName(keyCreateRequest.getKeyName())).thenReturn(0);
        Mockito.when(accessKeyMapper.createDtoToEntity(keyCreateRequest)).thenReturn(userKey);
        Mockito.when(keyHasher.encrypt(keyCreateRequest.getApiKey())).thenReturn(keyCreateRequest.getApiKey());
        Mockito.when(keyHasher.encrypt(keyCreateRequest.getSecreteKey())).thenReturn(keyCreateRequest.getSecreteKey());
        Mockito.when(binanceFutureRestService.getFuturesListenKey(Mockito.anyString()))
                .thenReturn(new ResponseEntity<>(Optional.of("listenKey"), HttpStatus.OK));
        Mockito.when(accessKeyRepository.save(userKey)).thenReturn(userKey);
        Mockito.when(accessKeyMapper.entityToCreateDtoResponse(userKey)).thenReturn(keyCreateResponse);
        KeyCreateResponse actualResp = accessKeyService.createKey(keyCreateRequest);

        Assertions.assertEquals(actualResp.getKeyName(), userKey.getKeyName());
    }

    @Test
    void getAllKeys() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        final Key userKey1 = new Key(
                1,
                "keyName",
                "apiKey",
                "secreteKey",
                new Date(),
                1
        );

        KeyDto keyDto = new KeyDto(userKey1.getKeyName(), userKey1.getApiKey(), userKey1.getSecreteKey(), userKey1.getCreateTime());

        final User user = new User(
                1,
                "firstName",
                "lastName",
                "mail@gmail.com",
                "password",
                Role.USER,
                List.of(userKey1)
        );

        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(accessKeyRepository.findAllByUser(user.getId())).thenReturn(List.of(userKey1));
        Mockito.when(accessKeyMapper.entityToGetDtoResponse(userKey1)).thenReturn(keyDto);
        Mockito.when(keyHasher.decrypt(keyDto.getApiKey())).thenReturn(keyDto.getApiKey());
        Mockito.when(keyHasher.decrypt(keyDto.getSecreteKey())).thenReturn(keyDto.getSecreteKey());

        KeyGetRequest actualResp = accessKeyService.getAllKeys(user.getEmail());

        Assertions.assertEquals(actualResp.getUserKeys().size(), 1);
    }

    @Test
    void updateKey() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        final Key userKey = new Key(
                1,
                "keyName",
                "apiKey",
                "secreteKey",
                new Date(),
                1
        );
        final User user = new User(
                1,
                "firstName",
                "lastName",
                "mail@gmail.com",
                "password",
                Role.USER,
                List.of(userKey)
        );

        KeyUpdateRequest keyUpdateRequest = new KeyUpdateRequest(
                new KeyDto(userKey.getKeyName(), userKey.getApiKey(), userKey.getSecreteKey(), userKey.getCreateTime()),
                userKey.getKeyName(),
                user.getEmail()
        );


        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(accessKeyRepository.findUserKey((keyUpdateRequest.getKeyName()), userKey.getUserId())).thenReturn(Optional.of(userKey));
        Mockito.when(keyHasher.encrypt(keyUpdateRequest.getKeyDto().getApiKey())).thenReturn(keyUpdateRequest.getKeyDto().getApiKey());
        Mockito.when(accessKeyRepository.save(userKey)).thenReturn(userKey);

        KeyUpdateResponse keyUpdateResponse = accessKeyService.updateKey(keyUpdateRequest);

        Assertions.assertTrue(keyUpdateResponse.isUpdated());
    }

    @Test
    void deleteKey() {
    }
}