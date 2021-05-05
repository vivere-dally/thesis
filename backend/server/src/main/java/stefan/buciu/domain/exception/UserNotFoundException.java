package stefan.buciu.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("The user with the given ID was not found");
    }
}
