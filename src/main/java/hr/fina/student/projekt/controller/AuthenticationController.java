package hr.fina.student.projekt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hr.fina.student.projekt.auth.AuthenticationResponse;
import hr.fina.student.projekt.auth.AuthenticationService;
import hr.fina.student.projekt.auth.LoginRequest;
import hr.fina.student.projekt.auth.RegisterRequest;
import hr.fina.student.projekt.dto.RegisterToUser;
import hr.fina.student.projekt.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RegisterToUser mapper;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        User user = mapper.mapToUser(request);
        return ResponseEntity.ok(authenticationService.register(user));
   
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
    
    
}
