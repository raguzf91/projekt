package hr.fina.student.projekt.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.dto.RegisterToUser;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.security.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserDao userRepository;
    private final RegisterToUser registerMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = registerMapper.mapToUser(request);
        user = User.builder()
            .updatedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        //if the email and password are correct
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow();
        
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
