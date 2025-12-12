package com.app.movie.Security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private final long jwtExpirationMs = 86400000; // 1 day

    // Generate JWT token with roles included
    public String generateToken(String email, Set<String> roles) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles.stream().collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, secretKey) // HS512 signature
                .compact();
    }

    // Get email (subject) from token
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Get roles from token
    public Set<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        Object roles = claims.get("roles");
        if (roles instanceof java.util.List<?> list) {
            return list.stream().map(Object::toString).collect(Collectors.toSet());
        }
        return Set.of();
    }

    // Validate token expiration
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
