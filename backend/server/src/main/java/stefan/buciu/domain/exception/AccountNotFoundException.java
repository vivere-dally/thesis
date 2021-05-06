package stefan.buciu.domain.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException() {
        super("The account with the given ID was not found");
    }
}
