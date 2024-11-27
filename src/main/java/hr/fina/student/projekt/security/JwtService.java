package hr.fina.student.projekt.security;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import hr.fina.student.projekt.entity.UserPrincipal;
import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Jwts.SIG;



@Service
@Slf4j
public class JwtService {
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final String secretKeyString = Encoders.BASE64.encode(SECRET_KEY.getEncoded());
    //"4ty0vsG5rV7cfDf6vtplXxt3Q6+uuxCXwfeMFfOrVGw="; //TODO napravi ENV VARIJABLU
    //in case the clock on the parsing machine is not perfectly in sync with the clock on the machine that created the JWT
    private static final long SECONDS = 3 * 60;
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_000; // 30 minuta 
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 3_600_000; // 60 minuta


    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
        
    }


    private Claims extractAllClaims(String token) {
        log.info("Extracting claims from the token");
        try {
            return Jwts.parser().clockSkewSeconds(SECONDS).verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
            
        } catch (JwtException e) {
            throw new UnsupportedJwtException("False token");
        } 
        
                
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    //TODO OVO PROVJERI
    private Map<String, Object> getClaimsFromUser(UserPrincipal userPrincipal) {
        String[] claims = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
        HashMap<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("authorities", claims);

        return claimsMap;
        
    } 

    private SecretKey getSigningKey() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString));
        return key;
       
    }

    public String getJwtFromRequest(String authHeader) {
        String bearerToken = authHeader.substring(7);
        return bearerToken;
    }

    public String createAccessToken(UserPrincipal userPrincipal) {
        //OVAJ BI TREBAO IMATI EXTRA CLAIMS AUTHORITIES USERA
        return generateAccessToken(userPrincipal);
    }

    public String createRefreshToken(UserPrincipal userPrincipal) {
        return generateRefreshToken(userPrincipal);
    }
    

    public String generateAccessToken(UserPrincipal user) {
        return Jwts
            .builder()
            .claim("authorities", getClaimsFromUser(user)) // mozda radi idk, VJV NE 
            .issuer("Filip")
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
            .signWith(getSigningKey(), SIG.HS256)
            .compact();
             
    }

    public String generateRefreshToken(UserPrincipal user) {
        return Jwts
            .builder()
            .issuer("Filip")
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(getSigningKey(), SIG.HS256)
            .compact();
             
    }

   
    //TODO
   public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
   }

    public boolean isTokenExpired(String token) {
        if(extractExpiration(token).before(new Date())) {
            return true;
        }
        return false;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    
    //TODO PRONAČI NAČIN KAKO EXTRACTAT AUTHORITIES (CUSTOM CLAIM) IZ TOKENA


    
}
