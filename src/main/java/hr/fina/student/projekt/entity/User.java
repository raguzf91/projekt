package hr.fina.student.projekt.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper=false)
public class User extends BaseEntity {
    
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String userGender;
    private String bio;
    private String phoneNumber;
    private List<String> languages;
    private Double responseRate;
    private String profilePhoto;
    private boolean enabled;
    private boolean accountLocked;
    private Role role;
    


   
    public String fullName() {
        return this.firstName + " " + this.lastName;
    }

  



    




}
