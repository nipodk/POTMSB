package com.poweroftwo.potms_backend.access_key.mapper;

import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyCreateRequest;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyCreateResponse;
import com.poweroftwo.potms_backend.access_key.controllers.dtos.KeyDto;
import com.poweroftwo.potms_backend.access_key.entity.Key;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccessKeyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "apiKey", ignore = true)
    @Mapping(target = "secreteKey", ignore = true)
    Key createDtoToEntity(KeyCreateRequest keyCreateRequest);
    KeyCreateResponse entityToCreateDtoResponse(Key key);
    @Mapping(target = "apiKey", ignore = true)
    @Mapping(target = "secreteKey", ignore = true)
    KeyDto entityToGetDtoResponse(Key key);

}
