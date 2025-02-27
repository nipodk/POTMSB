package com.poweroftwo.potms_backend.access_key.controllers.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class KeyCreateResponse {
    private String keyName;
    private Date createTime;
}
