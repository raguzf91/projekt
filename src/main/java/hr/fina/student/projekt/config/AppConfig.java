package hr.fina.student.projekt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import hr.fina.student.projekt.dao.impl.UserDaoImpl;
import hr.fina.student.projekt.dto.RegisterToUser;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserDaoImpl repository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username);
 }

    // Configure AuthenticationManagerBuilder to use the custom UserDetailsService
    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
     auth
         .userDetailsService(userDetailsService());
         .passwordEncoder(passwordEncoder())  
         
                // Set the PasswordEncoder
 }*/

    @Bean
     public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
 }

   @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Configure UserDetailsService and PasswordEncoder
        return config.getAuthenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
}

   
    
}
