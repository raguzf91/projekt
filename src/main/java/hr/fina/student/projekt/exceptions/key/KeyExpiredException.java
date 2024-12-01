package hr.fina.student.projekt.exceptions.key;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import hr.fina.student.projekt.enums.ErrorCode;
import hr.fina.student.projekt.exceptions.ApiException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class KeyExpiredException extends ApiException {

    public KeyExpiredException(String message) {
        super(message, ErrorCode.FORBIDDEN);
    }
   
}
