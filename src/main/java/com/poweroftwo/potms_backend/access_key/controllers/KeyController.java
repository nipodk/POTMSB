package com.poweroftwo.potms_backend.access_key.controllers;

import com.poweroftwo.potms_backend.access_key.controllers.dtos.*;
import com.poweroftwo.potms_backend.access_key.services.AccessKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
public class KeyController {
    private final AccessKeyService accessKeyService;

    @PostMapping
    public KeyCreateResponse createKey(@RequestBody KeyCreateRequest keyCreateRequest) throws Exception {
        return accessKeyService.createKey(keyCreateRequest);
    }

    @GetMapping
    public KeyGetRequest getAllKeys(@RequestParam String email) {
        return accessKeyService.getAllKeys(email);
    }

    @PutMapping
    public KeyUpdateResponse updateKey(@RequestBody KeyUpdateRequest keyUpdateRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return accessKeyService.updateKey(keyUpdateRequest);
    }

    @DeleteMapping
    public KeyRemoveResponse deleteKey(@RequestParam String userEmail, @RequestParam String keyName) {
        return accessKeyService.deleteKey(userEmail, keyName);
    }
}
