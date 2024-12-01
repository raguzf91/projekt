package hr.fina.student.projekt.exceptions.database;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import hr.fina.student.projekt.enums.ErrorCode;
import hr.fina.student.projekt.exceptions.ApiException;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseException  extends ApiException {

    public DatabaseException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_ERROR);
    }
    
}
