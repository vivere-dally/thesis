package stefan.buciu.domain.exception;

public class AccountHasInsufficientFundsException extends RuntimeException {

    public AccountHasInsufficientFundsException() {
        super("Could not complete the transaction because the account has insufficient funds.");
    }
}
