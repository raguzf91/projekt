package hr.fina.student.projekt.service.impl;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import hr.fina.student.projekt.entity.Role;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.entity.UserPrincipal;
import hr.fina.student.projekt.service.RoleService;
import hr.fina.student.projekt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOidcUserService extends OidcUserService {
    private final UserService userService;
    private final RoleService roleService;
   
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
       OidcUser oidcUser = super.loadUser(userRequest);
       Map<String, Object> attributes = oidcUser.getAttributes();
       log.info("Attributes: {}", attributes);
       String email = attributes.get("email").toString();
       User user = userService.findUserByEmail(email);
       if(user != null) {
            log.info("User with email: {} already exists", email);
            Role role = roleService.getRoleByUserId(user.getId());
            return new UserPrincipal(user, role, oidcUser.getIdToken(), oidcUser.getUserInfo());
       }

        String firstName = attributes.get("given_name").toString();
        String lastName = attributes.get("family_name").toString();
        String profilePhoto = attributes.get("picture").toString();
        String password = new BCryptPasswordEncoder().encode("password"); // DUMMY PASSWORD FOR GOOGLE LOGIN
        List<Map<String, String>> personalInfo = getPersonalInfo(userRequest);
        String phoneNumber = null;
        String birthdayString = null;
        String gender = null;
        Date birthDate = null;
         if(personalInfo != null && !personalInfo.isEmpty()) {
             
            phoneNumber = personalInfo.get(0).get("phoneNumber");
            birthdayString = personalInfo.get(1).get("birthday");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
            LocalDate birthLocalDate = LocalDate.parse(birthdayString, formatter);
            birthDate = Date.from(birthLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            gender = personalInfo.get(2).get("gender");
            }
       user = User.builder()
       .email(email)
       .firstName(firstName)
       .lastName(lastName)
       .profilePhoto(profilePhoto)
       .password(password)
       .phoneNumber(phoneNumber)
       .dateOfBirth(birthDate)
       .gender(gender)
       .oauth2User(true)
       .build();
       user = userService.createUser(user);
       Role role = roleService.getRoleByUserId(user.getId());
       return new UserPrincipal(user, role, oidcUser.getIdToken(), oidcUser.getUserInfo());

    }

    private List<Map<String, String>> getPersonalInfo(OidcUserRequest userRequest) {
        String accessToken = userRequest.getAccessToken().getTokenValue();
        WebClient webClient = WebClient.builder()
            .baseUrl("https://people.googleapis.com")
            .build();
        Map<String, Object> response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1/people/me")
                .queryParam("personFields", "birthdays,genders,phoneNumbers")
                .build())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();
        
        List<Map<String, String>> personalInfo = parsePersonalInfo(response); 
        return personalInfo;
            
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> parsePersonalInfo(Map<String, Object> response) {
    log.info("People API Response: {}", response);

    List<Map<String, String>> personalInfo = new ArrayList<>();
        
    List<Map<String, Object>> phoneNumbers = (List<Map<String, Object>>) response.get("phoneNumbers");
    if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
        String phone = (String) phoneNumbers.get(0).get("value");
        log.info("Phone: " + phone);
        personalInfo.add(Map.of("phoneNumber", phone));
    }
    
    
    List<Map<String, Object>> birthdays = (List<Map<String, Object>>) response.get("birthdays");
    if (birthdays != null && !birthdays.isEmpty()) {
        Map<String, Object> birthday = (Map<String, Object>) birthdays.get(0).get("date");
        if (birthday != null) {
            Integer year = (Integer) birthday.get("year");
            Integer month = (Integer) birthday.get("month");
            Integer day = (Integer) birthday.get("day");
            log.info("Birthday: " + year + "-" + month + "-" + day);
            personalInfo.add(Map.of("birthday", year + "-" + month + "-" + day));
        }
    }
    
    // Extract genders: usually a list of gender objects with a "value" field
    List<Map<String, Object>> genders = (List<Map<String, Object>>) response.get("genders");
    if (genders != null && !genders.isEmpty()) {
        String gender = (String) genders.get(0).get("value");
        log.info("Gender: " + gender);
        personalInfo.add(Map.of("gender", gender));
    }
    return personalInfo;
    }

   
    
}
