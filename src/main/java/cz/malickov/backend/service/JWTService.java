package cz.malickov.backend.service;


import cz.malickov.backend.entity.User;
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
import cz.malickov.backend.enums.Role;

@Service
public class JWTService {

    private final int JwtValidityMillis;

    private final String secretKey;
    private final UserRepository userRepository;

    // @TODO if deployed to Kubernates with scaling, secret key must be obtained from some secret store and rotated regularly
    public JWTService(UserRepository userRepository,
                      @Value("${security.JwtValidityMillis:900000}") long jwtValidityMillis
    ) {
        this.userRepository = userRepository;
        this.JwtValidityMillis = (int) jwtValidityMillis;

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            this.secretKey = Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateAuthToken(String email) {

        Map<String,Object> claims = new HashMap<>();

        Optional <User> user = userRepository.findByEmail(email);
        List <Role> role = new ArrayList<>();
        if(user.isPresent()){
            role = List.of(user.get().getRoleName());
        }
        claims.put("role", role);

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

    // validate against user
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
