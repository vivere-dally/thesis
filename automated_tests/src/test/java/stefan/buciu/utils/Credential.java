package stefan.buciu.utils;

public class Credential {
    public final String username;
    public final String password;
    public boolean isCreated;

    public Credential(boolean valid) {
        this.username = DataGenerator.username(valid);
        this.password = DataGenerator.password(valid);
        this.isCreated = false;
    }
}
