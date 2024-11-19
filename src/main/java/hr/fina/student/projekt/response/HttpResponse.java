package hr.fina.student.projekt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.util.Map;

import org.springframework.http.HttpStatus;
@Data
@SuperBuilder
@JsonInclude(NON_DEFAULT)
public class HttpResponse {
    protected String timeStamp;
    protected int statusCode;
    protected HttpStatus status;
    protected String reason;
    protected String message;
    protected String developerMessage;
    protected Map<?, ?> data;
}
