package stefan.buciu.utils;

public class AccountTestState {
    private static AccountTestState instance = null;

    public final Credential credential;

    private AccountTestState() {
        this.credential = new Credential(true);
    }

    public static AccountTestState getInstance() {
        if (instance == null) {
            instance = new AccountTestState();
        }

        return instance;
    }
}
