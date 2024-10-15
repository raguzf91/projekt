package hr.fina.student.projekt.security;

import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                
                //extract the jwt token from the Auth header
                    //contains the token
                    final String authHeader = request.getHeader("Authorization");
                    final String jwtToken;
                    final String userEmail;
                    if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    jwtToken = getJwtFromRequest(authHeader);
                    userEmail = jwtService.extractUsername(jwtToken);
                
                // VALIDATE THE USER IF THE USER IS NOT CONNECTED AND ADD HIM TO SECURITY CONTEXT HOLDER
                if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    //Validate the token
                    if(jwtService.validateToken(jwtToken, userDetails)) {
                        // Update the SecurityContextHolder
                        //Authenticated user
                        // credentials null because user is already validated through jwt
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                    }
                }

                filterChain.doFilter(request, response);
                
                    
                //Load the user details from the token and the database
                //set the authentication in the SecurityContextHolder
                //procees with next request
    }

    private String getJwtFromRequest(String authHeader) {
        String bearerToken = authHeader.substring(7);
        return bearerToken;
    }
    
}
