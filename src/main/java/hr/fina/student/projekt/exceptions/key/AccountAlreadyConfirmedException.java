package hr.fina.student.projekt.exceptions.key;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import hr.fina.student.projekt.enums.ErrorCode;
import hr.fina.student.projekt.exceptions.ApiException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountAlreadyConfirmedException extends ApiException {
    
    public AccountAlreadyConfirmedException(String message) {
        super(message, ErrorCode.BAD_REQUEST);
    }
}
