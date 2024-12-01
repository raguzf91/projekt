package hr.fina.student.projekt.exceptions.security;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import hr.fina.student.projekt.enums.ErrorCode;
import hr.fina.student.projekt.exceptions.ApiException;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class JwtTokenException extends ApiException{

    public JwtTokenException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_ERROR);
    }
    
}
