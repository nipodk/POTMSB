package com.poweroftwo.potms_backend.access_key.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyUpdateResponse {
    private KeyDto keyDto;
    private boolean isUpdated;
}
