package com.poweroftwo.potms_backend.access_key.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyCheckResponse {
    private String apiKey;
    private boolean valid;
}
