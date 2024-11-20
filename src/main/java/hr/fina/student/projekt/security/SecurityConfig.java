package hr.fina.student.projekt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final BCryptPasswordEncoder encoder;
    private final UserDetailsService userDetailsService;
    
    
/**
 * 
 * POST requests to /api/auth/**  are open to public, we allow unauthenticated user to access the login and register endpoint
 * Allow access to URLs that start with /user/ to users with USER role
 * Allow access to URLs that start with /admin/ to users with ADMIN role
 * any other request with require authentication
 * @param http
 * @return
 * @throws Exception
 */
@Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable) // disable csrf for stateless api
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/**")).permitAll()
				.requestMatchers("/user/**").hasRole("USER")       
				.requestMatchers("/admin/**").hasRole("ADMIN")     
				.anyRequest().authenticated()               
			)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
             
        
    return http.build();
 }

 @Bean
 public AuthenticationProvider authenticationProvider() {
     DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
     authProvider.setUserDetailsService(userDetailsService);
     authProvider.setPasswordEncoder(encoder);
     return authProvider;
 }

 @Bean
 public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
     return configuration.getAuthenticationManager();
 }

    

    
    
}
