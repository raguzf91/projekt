package hr.fina.student.projekt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hr.fina.student.projekt.auth.AuthenticationService;
import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.request.LoginRequest;
import hr.fina.student.projekt.response.HttpResponse;
import hr.fina.student.projekt.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody User user) {
        UserDTO userDto = userService.createUser(user);
         return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDto))
                        .message(String.format("User account created for user %s", user.getFirstName()))
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
   
    }

    @PostMapping("/login") 
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        UserDTO userDto = userService.findUserByEmail(loginRequest.getEmail());
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDto))
                        .message(String.format("Login Successfull"))
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    
    private URI getUri() {
        return URI.create(fromCurrentContextPath().path("/user/get/<userId>").toUriString()); 
    }
    
}
