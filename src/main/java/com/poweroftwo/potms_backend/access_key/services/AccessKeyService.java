package com.poweroftwo.potms_backend.access_key.services;

import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyCreateRequest;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyCreateResponse;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyDto;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyGetRequest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface AccessKeyService {
    KeyCreateResponse createKey(KeyCreateRequest keyCreateRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
    KeyGetRequest getAllKeys(String userEmail);
}
