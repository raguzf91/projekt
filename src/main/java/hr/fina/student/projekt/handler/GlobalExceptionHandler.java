package hr.fina.student.projekt.handler;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import hr.fina.student.projekt.enums.ErrorCode;
import hr.fina.student.projekt.exceptions.key.AccountAlreadyConfirmedException;
import hr.fina.student.projekt.exceptions.user.AccountLockedException;
import hr.fina.student.projekt.exceptions.user.UserAlreadyExistsException;
import hr.fina.student.projekt.exceptions.user.UserUnauthorizedException;
import hr.fina.student.projekt.utils.ExceptionResponse;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
   

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ExceptionResponse.builder()
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(ex.getMessage())
                    .build()
                );
        
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserUnauthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    ExceptionResponse.builder()
                    .errorCode(ex.getErrorCode().getCode())
                    .error(ex.getMessage())
                    .build()
                );
    }

    
@ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                    ExceptionResponse.builder()
                    .errorCode(ex.getErrorCode().getCode())
                    .error(ex.getMessage())
                    .build()
                );
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccountLockedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ExceptionResponse.builder()
                    .errorCode(ex.getErrorCode().getCode())
                    .error(ex.getMessage())
                    .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    ExceptionResponse.builder()
                    .errorCode(ErrorCode.BAD_CREDENTIALS.getCode())
                    .error(ex.getMessage())
                    .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException ex) {

        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach(
            error -> {
                String errorMessage = error.getDefaultMessage();
                errors.add(errorMessage);
            });
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    ExceptionResponse.builder()
                    .validationErrors(errors)
                    .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        log.error("Internal server error", ex.getCause().toString());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ExceptionResponse.builder()
                    .error("Internal server error")
                    .build()
                );
    }

    @ExceptionHandler(AccountAlreadyConfirmedException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccountAlreadyConfirmedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    ExceptionResponse.builder()
                    .errorCode(ex.getErrorCode().getCode())
                    .error(ex.getMessage())
                    .build()
                );
    }

    
    
}
