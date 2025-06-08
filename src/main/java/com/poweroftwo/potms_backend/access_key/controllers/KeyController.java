package com.poweroftwo.potms_backend.access_key.controllers;

import com.poweroftwo.potms_backend.access_key.controllers.dtos.*;
import com.poweroftwo.potms_backend.access_key.services.AccessKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "AccessKey Controller", description = "Manage API keys")
public class KeyController {
    private final AccessKeyService accessKeyService;

    @PostMapping
    @Operation(summary = "Create new API key for a specified user")
    public KeyCreateResponse createKey(@RequestBody KeyCreateRequest keyCreateRequest) throws Exception {
        return accessKeyService.createKey(keyCreateRequest);
    }

    @GetMapping
    @Operation(summary = "Get all API keys for a specified user")
    public KeyGetRequest getAllKeys(@RequestParam String email) {
        return accessKeyService.getAllKeys(email);
    }

    @PutMapping
    @Operation(summary = "Update API key for a specified user")
    public KeyUpdateResponse updateKey(@RequestBody KeyUpdateRequest keyUpdateRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return accessKeyService.updateKey(keyUpdateRequest);
    }

    @DeleteMapping
    @Operation(summary = "Delete API key for a specified user")
    public KeyRemoveResponse deleteKey(@RequestParam String userEmail, @RequestParam String keyName) {
        return accessKeyService.deleteKey(userEmail, keyName);
    }
}
