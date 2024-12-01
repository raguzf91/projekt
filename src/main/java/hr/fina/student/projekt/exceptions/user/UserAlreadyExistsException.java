package hr.fina.student.projekt.exceptions.user;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import hr.fina.student.projekt.enums.ErrorCode;
import hr.fina.student.projekt.exceptions.ApiException;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends ApiException {
    public UserAlreadyExistsException(String message) {
        super(message, ErrorCode.CONFLICT);
    }
    
}
