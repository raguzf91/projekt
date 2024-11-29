package hr.fina.student.projekt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hr.fina.student.projekt.dao.ActivationTokenDao;
import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.ActivationToken;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.entity.UserPrincipal;
import hr.fina.student.projekt.exceptions.ApiException;
import hr.fina.student.projekt.request.LoginRequest;
import hr.fina.student.projekt.request.RegisterRequest;
import hr.fina.student.projekt.response.HttpResponse;
import hr.fina.student.projekt.security.JwtService;
import hr.fina.student.projekt.service.EmailService;
import hr.fina.student.projekt.service.RoleService;
import hr.fina.student.projekt.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;
import java.net.URI;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import static hr.fina.student.projekt.mapper.UserDTOMapper.fromUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static hr.fina.student.projekt.mapper.RegisterRequestMapper.registerRequestToUser; 
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    
    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtService tokerProvider;
    private final ActivationTokenDao activationTokenRepository;
    private final EmailService emailService;
    private final String ACTIVATION_URL = "http://localhost:8080/api/auth/account-verification";
    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody @Valid RegisterRequest request) throws MessagingException {

        User user = userService.createUser(registerRequestToUser(request));

        // send verification mail
        sendVerificationEmail(user);
        // when verified enable user account -> user.setEnabled(true)
        //when enabled unlock account
        UserDTO userDTO = fromUser(user, roleService.getRoleByUserId(user.getId()));
         return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO))
                        .message(String.format("User account created for user %s", request.getFirstName()))
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
   
    }

    @PostMapping("/login") 
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        UserPrincipal user = authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        //OVDJE POŠALJI ACCESS I REFRESH TOKEN
        return sendResponse(user);
    }
        
        
    
    
    
    private URI getUri() {
        return URI.create(fromCurrentContextPath().path("/user/get/<userId>").toUriString()); 
    }

    private UserPrincipal authenticate(String email, String password) {
        log.info("authenticating user: " + email);
        //UserDTO userByEmail = userService.findUserByEmail(email);
        try {
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            UserPrincipal loggedInUser = getLoggedInUser(authentication);
            log.info("User role" + loggedInUser.getRole());
            return loggedInUser;
        } catch (Exception e) {
            log.error("Error authenticating user with email: " + email);
            throw new ApiException("Api Exception has occured");
        }
    }

    private UserPrincipal getLoggedInUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        UserDTO userDTO = fromUser(user, userPrincipal.getRole());
        return ResponseEntity.ok().body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(Map.of("user", userDTO, "access_token", tokerProvider.createAccessToken(getUserPrincipal(user)), 
                             "refresh_token", tokerProvider.createRefreshToken(getUserPrincipal(user)))) 
                .message("Login Success")
                .status(OK)
                .statusCode(OK.value())
                .build()     
        );
    }

    private UserPrincipal getUserPrincipal(User user) {
        return new UserPrincipal(userService.findUserByEmail(user.getEmail()), roleService.getRoleByUserId(user.getId()));
    }

    private void sendVerificationEmail(User user) throws MessagingException {
        String activationKey = generateAndSaveActivationToken(user);
        emailService.sendVerificationEmail(user.getFirstName(), user.getEmail(), ACTIVATION_URL, activationKey, "activateAccount");
    }
        
           
    private String generateAndSaveActivationToken(User user) {
         //generate key
         String generatedKey = generateActivationKey(6);
         ActivationToken activationToken = ActivationToken.builder()
                 .key(generatedKey)
                 .createdAt(LocalDateTime.now())
                 .expiresAt(LocalDateTime.now().plusMinutes(15))
                 .user(user)
                 .build();
        activationTokenRepository.saveActivationToken(activationToken);
        return generatedKey;
    }

    private String generateActivationKey(int length) {
        String characters = "0123456789";
         StringBuilder keyBuilder = new StringBuilder();
         SecureRandom secureRandom = new SecureRandom();
         for (int i = 0; i < length; i++) {
             int randomIndex = secureRandom.nextInt(characters.length()); //od 0 do 9
             keyBuilder.append(characters.charAt(randomIndex)); // u secureRandom dodamo broj na tom random indexu

         }

         return keyBuilder.toString();
    }
    
}


