package com.poweroftwo.potms_backend.access_key.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class KeyCreateRequest {
    private String keyName;
    private String apiKey;
    private String secreteKey;
    private Date createTime;
    private String userEmail;
}
