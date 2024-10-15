package hr.fina.student.projekt.dto;

import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;

import hr.fina.student.projekt.auth.RegisterRequest;
import hr.fina.student.projekt.entity.Role;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.enums.Gender;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterToUser {

    private final PasswordEncoder passwordEncoder;
    public User mapToUser(RegisterRequest registerRequest) {
        return User.builder()
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))  // encode password
            .dateOfBirth(registerRequest.getDate())
            .gender(Gender.valueOf(registerRequest.getGender().toUpperCase()))  // assuming Gender is an enum
            .phoneNumber(registerRequest.getPhoneNumber())
            .languages(registerRequest.getLanguages())
            .profilePhoto(registerRequest.getPhotoUrl())
            .roles(Set.of(new Role("ROLE_USER")))  // assigning default role to new users
            .build();
    }
    }

