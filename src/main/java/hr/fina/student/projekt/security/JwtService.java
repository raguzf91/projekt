package hr.fina.student.projekt.security;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts.SIG;


@Service
public class JwtService {
    private final String SECRET_KEY = "4ty0vsG5rV7cfDf6vtplXxt3Q6+uuxCXwfeMFfOrVGw=";

    public String extractUsername(String jwtToken) {
        Claims claims = extractAllClaims(jwtToken);
        return claims.getSubject();
    }

    // POTENCIJALNI PROBLEM OVDJE 
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
                
    }

    private SecretKey getSigningKey() {
       byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
       return Keys.hmacShaKeyFor(keyBytes);
       
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
            .builder()
            .claims()
            .add(extraClaims)
            .and()
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 10))
            .signWith(getSigningKey(), SIG.HS256)
            .compact();
             
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean usernameValid = username.equals(userDetails.getUsername());
        return usernameValid && !isTokenExpired(token); 
        //TODO VERIFY SIGNATURE OF THE TOKEN

    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        if(claims.getExpiration().before(new Date())) {
            return true;
        }
        return false;
    }
}
