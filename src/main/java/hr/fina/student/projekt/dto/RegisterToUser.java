package hr.fina.student.projekt.dto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import hr.fina.student.projekt.auth.RegisterRequest;
import hr.fina.student.projekt.entity.User;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class RegisterToUser {

    private final BCryptPasswordEncoder passwordEncoder;

    public User mapToUser(RegisterRequest registerRequest) {
        return User.builder()
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))  // encode password
            .dateOfBirth(Integer.valueOf(registerRequest.getDate()))
            .gender(registerRequest.getGender().toUpperCase())
            .phoneNumber(registerRequest.getPhoneNumber())
            .profilePhoto(registerRequest.getPhotoUrl())  // assigning default role to new users
            .build();
    }
    }

