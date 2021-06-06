package stefan.buciu.utils;

import com.github.javafaker.Faker;

public class DataGenerator {
    private static final Faker faker = Faker.instance();

    public static int USR_MIN_LEN = 3, USR_MAX_LEN = 16;
    public static int PWD_MIN_LEN = 8, PWD_MAX_LEN = 255;

    public static String password(boolean valid) {
        String password = "";
        if (valid) {
            password = faker.internet().password(PWD_MIN_LEN, PWD_MAX_LEN, true, true, true);
        }
        else {
            password = faker.internet().password(1, 7);
        }

        return password;
    }

    public static String username(boolean valid) {
        String username = "";
        if (valid) {
            username = faker.internet().password(USR_MIN_LEN, USR_MAX_LEN);
        }
        else {
            username = faker.internet().password(1, 2);
        }

        return username;
    }
}
