package com.poweroftwo.potms_backend.positions.dtos;

import com.poweroftwo.potms_backend.balance.websockets.client.dto.PositionData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class KeyPosition {
    String keyName;
    List<PositionData> positionData;
}