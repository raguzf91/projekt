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
    private String gender;
    private String bio;
    private String phoneNumber;
    private String[] speaksLanguages;
    private Integer responseRate;
    private String profilePhoto;
    private boolean enabled;
    private boolean accountLocked;
    private Role role;
    private Double averageRating;
    private String city;
    private String country;
    


   
    public String fullName() {
        return this.firstName + " " + this.lastName;
    }

  



    




}
