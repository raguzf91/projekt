package hr.fina.student.projekt.exceptions.user;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import hr.fina.student.projekt.enums.ErrorCode;
import hr.fina.student.projekt.exceptions.ApiException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends ApiException {
    public UserUnauthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED);
    }
    
}
