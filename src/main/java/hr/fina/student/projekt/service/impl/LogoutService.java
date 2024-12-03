package hr.fina.student.projekt.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import hr.fina.student.projekt.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler{

    
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //extract the jwt token from the Auth header
        final String authHeader = request.getHeader("AUTHORIZATION");
        final String jwtToken;
    try {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        
        jwtToken = jwtService.getJwtFromRequest(authHeader);
    }
    
    }
}
