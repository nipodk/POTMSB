package com.poweroftwo.potms_backend.access_key.services;

import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyCreateRequest;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyCreateResponse;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyDto;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyGetRequest;
import com.poweroftwo.potms_backend.access_key.entity.Key;
import com.poweroftwo.potms_backend.access_key.mapper.AccessKeyMapper;
import com.poweroftwo.potms_backend.access_key.repository.AccessKeyRepository;
import com.poweroftwo.potms_backend.user.repository.UserRepository;
import com.poweroftwo.potms_backend.user.repository.entities.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


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
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public KeyCreateResponse createKey(KeyCreateRequest keyCreateRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
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
        final Key keyCreateResponse = accessKeyRepository.save(key);
        return accessKeyMapper.entityToCreateDtoResponse(keyCreateResponse);
    }

    @Override
    @Transactional
    public KeyGetRequest getAllKeys(String userEmail) {
        final User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("This user doesn't exists"));
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
                keyDto.setSecreteKey(keyHasher.decrypt(key.getSecreteKey()));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("Can't decrypt the secrete key");
            }
            return keyDto;
        }).toList();
        return new KeyGetRequest(userKeysInfo, userEmail);
    }
}
