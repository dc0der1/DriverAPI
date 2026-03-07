package org.example.droppydriver.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.droppydriver.models.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService implements IJwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    /**
     * Generates a token when the user logs in
     *
     * @param userModel contains user's UUID
     * @return the generated token
     */
    @Override
    public String generateToken(UserModel userModel) {
        return Jwts.builder()
                .subject(userModel.getId().toString())
                .claim("roles", userModel.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 84600000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates the token
     *
     * @param token contains the generated token
     * @return the id*/
    @Override
    public UUID validateToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UUID.fromString(claims.getSubject());
    }

    /**
     * Gets the signing key
     *
     * @return the signing key*/
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
