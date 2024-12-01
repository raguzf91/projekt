package hr.fina.student.projekt.exceptions.user;

import hr.fina.student.projekt.enums.ErrorCode;
import hr.fina.student.projekt.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountLockedException extends ApiException {

    public AccountLockedException(String message) {
        super(message, ErrorCode.FORBIDDEN);
    }

   
}
 