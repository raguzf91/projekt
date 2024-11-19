package hr.fina.student.projekt.security;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import hr.fina.student.projekt.entity.UserPrincipal;
import java.util.Map;
import java.util.HashMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Jwts.SIG;



@Service
@Slf4j
public class JwtService {
    private final String SECRET_KEY = "4ty0vsG5rV7cfDf6vtplXxt3Q6+uuxCXwfeMFfOrVGw="; //TODO napravi ENV VARIJABLU

    public String extractUsername(String jwtToken) {
        Claims claims = extractAllClaims(jwtToken);
        return claims.getSubject();
    }

    // POTENCIJALNI PROBLEM OVDJE 
    private Claims extractAllClaims(String token) {
        log.info("Extracting claims from the token");
        try {
            return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
            
        } catch (JwtException e) {
            throw new UnsupportedJwtException("False token");
        }
        
                
    }

    //TODO
    private Map<String, Object> getClaimsFromUser(UserPrincipal userPrincipal) {
        String[] claims = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
        HashMap<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("authorities", claims);

        return claimsMap;
        
    } 

    private SecretKey getSigningKey() {
       byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
       return Keys.hmacShaKeyFor(keyBytes);
       
    }

    public String generateToken(UserPrincipal user) {
        return Jwts
            .builder()
            .claims(getClaimsFromUser(user))
            .issuer("Filip")
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 10))
            .signWith(getSigningKey(), SIG.HS256)
            .compact();
             
    }

    
   

    public boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        if(claims.getExpiration().before(new Date())) {
            return true;
        }
        return false;
    }
}
