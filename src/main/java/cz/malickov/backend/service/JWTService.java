package cz.malickov.backend.service;

import cz.malickov.backend.enums.Role;
import cz.malickov.backend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;


@Service
public class JWTService {

    @Value("${security.JwtValidityMillis}")
    private int JwtValidityMillis;

    private String secretKey;
    private UserRepository userRepository;

    // @TODO Create secretkey randomly do not hardcode it, it is hardcoded for convenience of testing
    public JWTService(UserRepository userRepository) {
        this.userRepository = userRepository;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            this.secretKey = "6/VrpJJUITHdOeWm8kykDJ3EzGmToaaRziM3af+JoAk=";//Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }


    public String generateToken(String email) {

        Map<String,Object> claims = new HashMap<>();
        List role = List.of(userRepository.findByEmail(email).get().getRoleName());
        claims.put("roles", role);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)  // I am not using user name
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JwtValidityMillis))
                .and()
                .signWith(getKey())
                .compact();
    }


    private SecretKey getKey() {
        byte [] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token) {
        return this.extractClaim(token, Claims::getSubject); // subject is email
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = this.extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
