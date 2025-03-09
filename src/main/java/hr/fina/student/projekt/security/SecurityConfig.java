package hr.fina.student.projekt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import lombok.RequiredArgsConstructor;
import hr.fina.student.projekt.entity.UserPrincipal;
import hr.fina.student.projekt.service.impl.CustomOidcUserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final BCryptPasswordEncoder encoder;
    private final UserDetailsService userDetailsService;
    private final LogoutHandler logoutHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CustomOidcUserService customOauthUserService;
    private final JwtService jwtService;
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
 public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/auth/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/listing/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/amenity/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/user/**")).permitAll().requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
            
            .requestMatchers(HttpMethod.POST, "/api/listing/create").hasAuthority("CREATE:LISTING")
            .requestMatchers(HttpMethod.POST, "/api/user/{id}/edit-profile").hasAuthority("EDIT:PROFILE")
            .requestMatchers(HttpMethod.DELETE, "/api/user/{id}/reservations/{reservationId}").hasAuthority("DELETE:RESERVATION")
            .requestMatchers(HttpMethod.POST, "/api/user/{id}/like-listing/{listingId}").hasAuthority("LIKE:LISTING")
            .requestMatchers(HttpMethod.POST, "/api/listing/{id}/booking").hasAuthority("RESERVE:LISTING")
           

			.anyRequest().authenticated()               
			)
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorizationEndpoint ->
                    authorizationEndpoint.authorizationRequestResolver(oauth2AuthorizationRequestResolver(clientRegistrationRepository))
                )
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.oidcUserService(customOauthUserService))
                .successHandler((request, response, authentication) -> {
                    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                    String token = jwtService.createAccessToken(userPrincipal);
                    response.sendRedirect("http://localhost:5173?token=" + token);
                })
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(Customizer.withDefaults())
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(
                    (request, response, authentication) -> {
                        SecurityContextHolder.clearContext();
                        response.setStatus(200);

                    }
                )
            );
    return http.build();
 }


 @Bean
 @Lazy
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

 @Bean
public OAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
    DefaultOAuth2AuthorizationRequestResolver defaultResolver =
            new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    defaultResolver.setAuthorizationRequestCustomizer(customizer ->
            customizer.additionalParameters(params -> params.put("prompt", "select_account")));
    return defaultResolver;
}

}
