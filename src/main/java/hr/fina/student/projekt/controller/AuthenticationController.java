package hr.fina.student.projekt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.entity.UserPrincipal;
import hr.fina.student.projekt.exceptions.ApiException;
import hr.fina.student.projekt.request.LoginRequest;
import hr.fina.student.projekt.response.HttpResponse;
import hr.fina.student.projekt.security.JwtService;
import hr.fina.student.projekt.service.RoleService;
import hr.fina.student.projekt.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;
import java.net.URI;
import java.util.Map;
import static hr.fina.student.projekt.mapper.UserDTOMapper.toUser; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    
    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtService tokerProvider;
    
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
        UserDTO user = authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        //OVDJE POŠALJI ACCESS I REFRESH TOKEN
        return sendResponse(user);
    }
        
        
    
    
    
    private URI getUri() {
        return URI.create(fromCurrentContextPath().path("/user/get/<userId>").toUriString()); 
    }

    private UserDTO authenticate(String email, String password) {
        log.info("authenticating user: " + email);
        //UserDTO userByEmail = userService.findUserByEmail(email);
        try {
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            UserDTO loggedInUser = getLoggedInUser(authentication);
            return loggedInUser;
        } catch (Exception e) {
            log.error("Error authenticating user with email: " + email);
            throw new ApiException("Api Exception has occured");
        }
    }

    private UserDTO getLoggedInUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(Map.of("user", user, "access_token", tokerProvider.createAccessToken(getUserPrincipal(user)), 
                             "refresh_token", tokerProvider.createRefreshToken(getUserPrincipal(user)))) 
                .message("Login Success")
                .status(OK)
                .statusCode(OK.value())
                .build()     
        );
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(toUser(userService.findUserByEmail(user.getEmail())), roleService.getRoleByUserId(user.getId()));
    }

    
}


