package com.poweroftwo.potms_backend.authentication.services;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUserName(String token);
    String generateToken(UserDetails userDetails);
    boolean isTokenValid(String token, UserDetails userDetails);
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);


}
