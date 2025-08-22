package com.example.competr.portal.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        System.out.println("[JwtUtil] Extracting username from token");
        System.out.println("[JwtUtil] Secret key length: " + (SECRET_KEY != null ? SECRET_KEY.length() : "NULL"));
        String username = getClaims(token).getSubject();
        System.out.println("[JwtUtil] Username extracted: " + username);
        return username;
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        System.out.println("[JwtUtil] Parsing JWT token");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("[JwtUtil] Token parsed successfully");
            return claims;
        } catch (Exception e) {
            System.out.println("[JwtUtil] Error parsing token: " + e.getMessage());
            throw e;
        }
    }
}
