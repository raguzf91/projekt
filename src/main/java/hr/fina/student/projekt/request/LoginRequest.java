package hr.fina.student.projekt.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class LoginRequest {
    
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
