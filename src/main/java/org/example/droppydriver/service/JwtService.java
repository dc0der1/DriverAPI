package org.example.droppydriver.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class JwtService {

    private static final Algorithm algorithm = Algorithm.HMAC256("secret");
    private static final JWTVerifier verifier = JWT.require(algorithm).build();

    public String generateToken(UUID userId) {
        return JWT.create()
                .withIssuer("DroppyDriver")
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(60, ChronoUnit.MINUTES))
                .withSubject(userId.toString())
                .sign(algorithm);
    }

    public UUID validateToken(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return UUID.fromString(jwt.getSubject());
    }

}
