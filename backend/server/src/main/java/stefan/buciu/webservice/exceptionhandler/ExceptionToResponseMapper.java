package stefan.buciu.webservice.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import stefan.buciu.domain.exception.*;

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

    //region Account Errors
    @ExceptionHandler(value = AccountHasInsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleAccountHasInsufficientFunds(AccountHasInsufficientFundsException exception) {
        return getErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException exception) {
        return getErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    //endregion

    @ExceptionHandler(value = EntitySocketNotificationException.class)
    public ResponseEntity<ErrorResponse> handleEntitySocketNotification(EntitySocketNotificationException exception) {
        return getErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
