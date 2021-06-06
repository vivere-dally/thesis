package stefan.buciu.utils;

public class AccountTestState {
    private static AccountTestState instance = null;

    public final String username;
    public final String password;
    public boolean first;

    private AccountTestState() {
        this.username = DataGenerator.username(true);
        this.password = DataGenerator.password(true);
        first = true;
    }

    public static AccountTestState getInstance() {
        if (instance == null) {
            instance = new AccountTestState();
        }

        return instance;
    }
}
