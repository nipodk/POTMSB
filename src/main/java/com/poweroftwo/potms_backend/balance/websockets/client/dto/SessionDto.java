package com.poweroftwo.potms_backend.balance.websockets.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
public class SessionDto {
    private WebSocketSession webSocketSession;
    private String email;
}
