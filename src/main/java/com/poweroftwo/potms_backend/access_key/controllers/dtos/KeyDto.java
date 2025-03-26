package com.poweroftwo.potms_backend.access_key.controllers.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class KeyDto {
    private String keyName;
    private String apiKey;
    private String secreteKey;
    private Date createTime;
}
