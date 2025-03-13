package com.poweroftwo.potms_backend.access_key.services;

import com.poweroftwo.potms_backend.access_key.controllers.dtos.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface AccessKeyService {
    KeyCreateResponse createKey(KeyCreateRequest keyCreateRequest) throws Exception;
    KeyGetRequest getAllKeys(String userEmail);
    KeyUpdateResponse updateKey(KeyUpdateRequest keyUpdateRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
    KeyRemoveResponse deleteKey(String userEmail, String keyName);
}
