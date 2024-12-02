package hr.fina.student.projekt.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    

    BAD_REQUEST(400, "Invalid request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "Forbidden", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "Not found", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_ALLOWED(405, "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    CONFLICT(409, "Conflict with current state of data", HttpStatus.CONFLICT),
    ACCOUNT_LOCKED(302, "User Account is locked", HttpStatus.FORBIDDEN),
    BAD_CREDENTIALS(303, "Email or password is incorrect", HttpStatus.UNAUTHORIZED),
    ACCOUNT_ALREADY_CONFIRMED(304, "Account is already confirmed", HttpStatus.BAD_REQUEST);
   

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


}
