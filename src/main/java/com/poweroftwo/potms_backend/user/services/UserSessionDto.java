package com.poweroftwo.potms_backend.user.services;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSessionDto {
    private String listenKey;
    private String listenKeyName;
    private String email;
}
