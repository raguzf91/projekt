package hr.fina.student.projekt.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String gender;
    private String bio;
    private String phoneNumber;
    private List<String> languages;
    private Double responseRate;
    private String profilePhoto;
    private boolean enabled;
    private boolean accountLocked;
    private String roleName;
    private String permissions;

}
