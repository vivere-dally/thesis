package stefan.buciu.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("A user with the given username already exists.");
    }
}
