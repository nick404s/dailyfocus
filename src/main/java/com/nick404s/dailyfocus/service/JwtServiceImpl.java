package com.nick404s.dailyfocus.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService{

    // jwt values from the app properties
    @Value("${spring.jwt.secret}")
    private String SECRET_KEY;
    @Value("${spring.jwt.expiration}")
    private long JWT_EXPIRATION;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // get only the subject of username
    }

    // Generic helper method to extract a claim
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false; // return false in case of expired, malformed etc.
        }
    }

    private boolean isTokenExpired(String token){
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder().claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis())) // issued at the current time of the system
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) // expiring starting with the current time + the expiration time
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // convert the secret key to bytes
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
