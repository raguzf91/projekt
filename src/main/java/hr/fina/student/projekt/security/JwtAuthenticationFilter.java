package hr.fina.student.projekt.security;


import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import hr.fina.student.projekt.entity.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import static hr.fina.student.projekt.exceptions.ExceptionUtils.processError;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                
                //extract the jwt token from the Auth header
                    final String authHeader = request.getHeader("AUTHORIZATION");
                    final String jwtToken;
                    final String userEmail;
                try {
                    if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                    
                    jwtToken = jwtService.getJwtFromRequest(authHeader);
                    userEmail = jwtService.extractUsername(jwtToken);
                    
                    
                // VALIDATE THE USER IF THE USER IS NOT CONNECTED AND ADD HIM TO SECURITY CONTEXT HOLDER
                if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    UserPrincipal userPrincipal = (UserPrincipal)this.userDetailsService.loadUserByUsername(userEmail);

                    //Validate the token 
                    if(jwtService.isTokenValid(jwtToken, userPrincipal)) {
            
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userPrincipal,null, userPrincipal.getAuthorities()); 
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                    }
                }

                filterChain.doFilter(request, response);
                
                } catch(Exception e) {
                    processError(request, response, e);

                }
                    
    }

    
}
