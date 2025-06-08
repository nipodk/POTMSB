package com.poweroftwo.potms_backend.positions.services;

import com.poweroftwo.potms_backend.balance.websockets.client.dto.PositionData;
import com.poweroftwo.potms_backend.balance.websockets.client.mapper.PositionDataMapper;
import com.poweroftwo.potms_backend.positions.dtos.KeyPosition;
import com.poweroftwo.potms_backend.positions.dtos.UserPositions;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPositionsMapper {
    private final PositionDataMapper positionDataMapper;
    public UserPositions convertToUserPositions(Map<String, String> positionsData, String email){
        final UserPositions userPositions = new UserPositions();
        userPositions.setPositions(new ArrayList<>());
        positionsData.forEach(
                (keyName, keyPosition) -> {
                    JSONArray jsonArray = new JSONArray(keyPosition);
                    final List<PositionData> positionData = new ArrayList<>();
                    jsonArray.forEach(position -> {
                        PositionData userPosition = positionDataMapper.parseToPositionData((JSONObject) position);
                        if(userPosition.getPositionAmt() != 0){
                            positionData.add(userPosition);
                        }
                    });
                    userPositions.getPositions().add(new KeyPosition(keyName, positionData));
                    userPositions.setEmail(email);
                }

        );
        return userPositions;
    }
}