package stefan.buciu.webservice.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import stefan.buciu.domain.exception.UserAlreadyExistsException;
import stefan.buciu.domain.exception.UserNotFoundException;

@ControllerAdvice
public class ExceptionToResponseMapper extends ResponseEntityExceptionHandler {

    private ResponseEntity<ErrorResponse> getErrorResponse(RuntimeException exception, HttpStatus httpStatus) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), exception.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    //region User Errors
    @ExceptionHandler(value = UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException exception) {
        return getErrorResponse(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException exception) {
        return getErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    //endregion
}
