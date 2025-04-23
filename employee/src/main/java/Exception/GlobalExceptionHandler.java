package Exception;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        
        String errorMessage = ex.getMostSpecificCause().getMessage().toLowerCase();
        
        if (errorMessage.contains("user_user_name_key") || errorMessage.contains("username")) {
            errorResponse.put("error", "UserNameExist");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        } else if (errorMessage.contains("user_email_key") || errorMessage.contains("email")) {
            errorResponse.put("error", "EmailExist");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        } else if (errorMessage.contains("user_user_first_name_key")) {
            errorResponse.put("error", "FirstNameExist");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        
        errorResponse.put("error", "Database constraint violation");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}