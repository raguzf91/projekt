package hr.fina.student.projekt.exceptions;

import hr.fina.student.projekt.enums.ErrorCode;

public class ApiException extends RuntimeException {
    
        private final ErrorCode errorCode;
    
        public ApiException(String message, ErrorCode errorCode) {
            super(message);
            this.errorCode = errorCode;
        }
    
        public ErrorCode getErrorCode() {
            return errorCode;
        }
}
