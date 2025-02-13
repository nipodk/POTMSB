package com.poweroftwo.potms_backend.authentication.services;

import com.poweroftwo.potms_backend.authentication.services.dtos.AuthenticationRequest;
import com.poweroftwo.potms_backend.authentication.services.dtos.AuthenticationResponse;
import com.poweroftwo.potms_backend.authentication.services.dtos.RegisterRequest;
import com.poweroftwo.potms_backend.user.repository.UserRepository;
import com.poweroftwo.potms_backend.user.repository.entities.Role;
import com.poweroftwo.potms_backend.user.repository.entities.User;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        final Optional<User> foundUser = userRepository.findByEmail(request.getEmail());
        if(foundUser.isEmpty()) {
            final User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            final String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse
                    .builder()
                    .token(jwtToken)
                    .build();
        }
        else {
            throw new EntityExistsException("This user is already registered");
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        final User user = userRepository.findByEmail(request.getEmail()).orElseThrow(EntityNotFoundException::new);
        final String token = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }
}
