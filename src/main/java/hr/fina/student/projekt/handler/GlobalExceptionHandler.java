package hr.fina.student.projekt.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import hr.fina.student.projekt.utils.ExceptionResponse;
import jakarta.mail.MessagingException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    public ResponseEntity<ExceptionResponse> handleException() {
        return null;
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleMessagingException(MessagingException ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("Error sending email")
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    
}
