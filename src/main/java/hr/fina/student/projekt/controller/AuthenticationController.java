package hr.fina.student.projekt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.entity.UserPrincipal;
import hr.fina.student.projekt.exceptions.ApiException;
import hr.fina.student.projekt.exceptions.user.UserUnauthorizedException;
import hr.fina.student.projekt.request.LoginRequest;
import hr.fina.student.projekt.request.RegisterRequest;
import hr.fina.student.projekt.response.HttpResponse;
import hr.fina.student.projekt.security.JwtService;
import hr.fina.student.projekt.service.RoleService;
import hr.fina.student.projekt.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import static hr.fina.student.projekt.mapper.UserDTOMapper.fromUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody @Valid RegisterRequest request) throws MessagingException {

        User user = userService.createUser(registerRequestToUser(request));

        // send verification mail
        //TODO url je link na koji će activate account button u emailu redirectati korisnika - STAVI GA U FINALNU VARIJABLU
        sendEmail(user, "http://localhost:8080/api/auth/activate-account}" , "activateAccount");
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
    public RedirectView login(@RequestBody @Valid LoginRequest loginRequest) throws MessagingException, UnsupportedEncodingException {
        UserPrincipal user = authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        sendEmail(user.getUser(), "http://localhost:8080/api/auth/verify/code/" + user.getUser().getEmail() + "/", "verifyAccount");
        
        String redirectUrl = "/VerifyAccount.html?email=" + URLEncoder.encode(user.getUser().getEmail(), StandardCharsets.UTF_8.toString());
        return new RedirectView(redirectUrl);
        }
        
        @GetMapping("/verifyAccount")
        public String verifyAccountPage(@RequestParam String email, Model model) {
            model.addAttribute("email", email);
            return "VerifyAccount";
        }
        
    
        
    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        
        User user = userService.findUserByEmail(email);
        UserDTO userDTO = fromUser(user, roleService.getRoleByUserId(user.getId()));
        userService.verifyAccount(email, code);
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
        //OVDJE POŠALJI ACCESS I REFRESH TOKEN
        
    }

    @GetMapping("/activate-account")
    public ResponseEntity<HttpResponse> confirmAccount(@RequestParam String email, @RequestParam String key) throws MessagingException {
        try {
            userService.activateAccount(email, key);
            return ResponseEntity.ok(
                HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .message("Account successfully activated")
                    .status(HttpStatus.OK)
                    .statusCode(HttpStatus.OK.value())
                    .build()
            );
        } catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build()
            );
        }

        
        
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
           throw new BadCredentialsException("Pogrešni email ili lozinka");
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

   

    private void sendEmail(User user, String url, String emailType) throws MessagingException {
        userService.sendEmail(user, url, emailType);
    }
        
           
    
    
}


