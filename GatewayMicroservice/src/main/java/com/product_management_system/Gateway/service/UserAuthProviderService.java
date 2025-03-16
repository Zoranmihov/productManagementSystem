package com.product_management_system.Gateway.service;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.product_management_system.Gateway.dtos.UserJwtDTO;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component
public class UserAuthProviderService {

    @Value("${jwt_secret:default-secret-key}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Mono<Authentication> validateTokenAsync(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            UserJwtDTO user = new UserJwtDTO(
                decodedJWT.getClaim("id").asString(),
                decodedJWT.getClaim("role").asString()
            );

            return Mono.just(new UsernamePasswordAuthenticationToken(
                    user,
                    token,
                    Collections.singletonList(() -> "ROLE_" + user.getRole())
            ));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public String refreshToken(String oldToken) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        DecodedJWT decodedJWT = verifier.verify(oldToken);

        Date now = new Date();
        Date expiresAt = decodedJWT.getExpiresAt();

        long timeToExpiry = expiresAt.getTime() - now.getTime();
        if (timeToExpiry > 20 * 60 * 1000) {
            return oldToken; // Don't refresh if more than 20 minutes left
        }

        Date validity = new Date(now.getTime() + 10800000); // 3 hours

        return JWT.create()
                .withIssuer(decodedJWT.getIssuer())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("id", decodedJWT.getClaim("id").asString())
                .withClaim("role", decodedJWT.getClaim("role").asString())
                .sign(Algorithm.HMAC256(secretKey));
    }

    public ResponseCookie createJwtCookie(String token) {
        return ResponseCookie.from("JWT", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(10803) // slightly over 3 hours
                .build();
    }

    public String getSenderId(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT.getClaim("id").asString();
    }
}