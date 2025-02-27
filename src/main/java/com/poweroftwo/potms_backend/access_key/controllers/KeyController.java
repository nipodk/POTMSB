package com.poweroftwo.potms_backend.access_key.controllers;

import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyCreateRequest;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyCreateResponse;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyDto;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyGetRequest;
import com.poweroftwo.potms_backend.access_key.services.AccessKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/key")
@RequiredArgsConstructor
public class KeyController {
    private final AccessKeyService accessKeyService;

    @PostMapping
    public KeyCreateResponse createKey(@RequestBody KeyCreateRequest keyCreateRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return accessKeyService.createKey(keyCreateRequest);
    }

    @GetMapping
    public KeyGetRequest getAllKeys(@RequestParam String email) {
        return accessKeyService.getAllKeys(email);
    }
}
