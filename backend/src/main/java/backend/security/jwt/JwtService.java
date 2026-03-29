package backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.UUID;


@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generating Token
    public String generateToken(UUID developerId, String email, String role){
        return Jwts.builder()
                .setSubject(developerId.toString())
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extracting Developer ID from the token
    public UUID extractDeveloperId(String token){
        String subject = extractAllClaims(token).getSubject();
        return UUID.fromString(subject);
    }

    // Extracting Email
    public String extractEmail(String token){
        return extractAllClaims(token).get("email", String.class);
    }

    // Validating the Token
    public boolean isTokenValid(String token){
        try {
            extractAllClaims(token); // If invalid, it will throw an exception
            return true;
        } catch (Exception e){
            return false;
        }
    }

    // Extracting Role
    public String extractRole(String token){
        return extractAllClaims(token).get("role", String.class);
    }

    // The Internal method
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }







}
