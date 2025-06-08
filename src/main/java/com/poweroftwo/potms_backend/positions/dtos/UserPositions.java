package com.poweroftwo.potms_backend.positions.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserPositions {
    String email;
    List<KeyPosition> positions;
}