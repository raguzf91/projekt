package hr.fina.student.projekt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final DataSource dataSource;

    @Bean
 public UserDetailsService userDetailsService() {
     JdbcDaoImpl jdbcUserDetailsService = new JdbcDaoImpl();
     jdbcUserDetailsService.setDataSource(dataSource); // Set DataSource

     // Customize the queries if needed
     jdbcUserDetailsService.setUsersByUsernameQuery("select email, password, enabled from users where email=?");
     jdbcUserDetailsService.setAuthoritiesByUsernameQuery("select email, role from roles where email=?");

     return jdbcUserDetailsService;
 }

 // Configure AuthenticationManagerBuilder to use the custom UserDetailsService
 @Autowired
 public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
     auth
         .userDetailsService(userDetailsService());  
         
                // Set the PasswordEncoder
 }

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
