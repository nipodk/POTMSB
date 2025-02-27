package com.poweroftwo.potms_backend.authentication.controllers;

import com.poweroftwo.potms_backend.authentication.services.AuthenticationService;
import com.poweroftwo.potms_backend.authentication.services.dtos.AuthenticationRequest;
import com.poweroftwo.potms_backend.authentication.services.dtos.AuthenticationResponse;
import com.poweroftwo.potms_backend.authentication.services.dtos.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
