package com.example.emortion_journal.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretBase64;

    @Value("${jwt.expirationSeconds:3600}")
    private long expirationSeconds;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64); // 32バイト以上(Base64)
        return Keys.hmacShaKeyFor(keyBytes); // alg=HmacSHA256 が入った SecretKey
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(getSigningKey()) // ← アルゴリズム指定不要（推論）
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token, String expectedUsername) {
        try {
            return expectedUsername != null && expectedUsername.equals(getUsernameFromToken(token));
        } catch (Exception e) {
            return false;
        }
    }
}
