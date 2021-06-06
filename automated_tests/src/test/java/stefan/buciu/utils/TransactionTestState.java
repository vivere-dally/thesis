package stefan.buciu.utils;

public class TransactionTestState {
    private static TransactionTestState instance = null;

    public final Credential credential;
    public final String accountMoney;
    public final String accountMonthlyIncome;

    private TransactionTestState() {
        this.credential = new Credential(true);
        this.accountMoney = DataGenerator.money(true);
        this.accountMonthlyIncome = DataGenerator.money(true);
    }

    public static TransactionTestState getInstance() {
        if (instance == null) {
            instance = new TransactionTestState();
        }

        return instance;
    }
}
