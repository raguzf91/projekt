package hr.fina.student.projekt.auth;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String gender;
    private LocalDate date;
    private String phoneNumber;
    private List<String> languages;
    private String photoUrl;
}
