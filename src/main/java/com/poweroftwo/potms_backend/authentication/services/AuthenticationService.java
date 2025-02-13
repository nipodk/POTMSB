package com.poweroftwo.potms_backend.authentication.services;

import com.poweroftwo.potms_backend.authentication.services.dtos.AuthenticationRequest;
import com.poweroftwo.potms_backend.authentication.services.dtos.AuthenticationResponse;
import com.poweroftwo.potms_backend.authentication.services.dtos.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
