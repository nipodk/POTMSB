package com.poweroftwo.potms_backend.access_key.controllers.dtos;

import lombok.Data;

@Data
public class KeyUpdateRequest {
    private KeyDto keyDto;
    private String keyName;
    private String userEmail;
}
