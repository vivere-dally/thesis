package stefan.buciu.utils;

import com.github.javafaker.Faker;

public class DataGenerator {
    private static final Faker faker = Faker.instance();

    public static int USR_MIN_LEN = 3, USR_MAX_LEN = 16;
    public static int PWD_MIN_LEN = 8, PWD_MAX_LEN = 255;
    public static int MONEY_MIN_VALUE = 10, MONEY_MAX_VALUE = 999999;
    public static int MONEY_DECIMAL_MIN_VALUE = 11, MONEY_DECIMAL_MAX_VALUE = 99;

    public static String password(boolean valid) {
        if (valid) {
            return faker.internet().password(PWD_MIN_LEN, PWD_MAX_LEN, true, true, true);
        }

        return faker.internet().password(1, 7);
    }

    public static String username(boolean valid) {
        if (valid) {
            return faker.internet().password(USR_MIN_LEN, USR_MAX_LEN);
        }

        return faker.internet().password(1, 2);
    }

    public static String money(boolean valid) {
        if (valid) {
            return String.format("%d.%d",
                    faker.number().numberBetween(MONEY_MIN_VALUE, MONEY_MAX_VALUE),
                    faker.number().numberBetween(MONEY_DECIMAL_MIN_VALUE, MONEY_DECIMAL_MAX_VALUE));
        }

        return String.format("%d.%d",
                faker.number().numberBetween(MONEY_MIN_VALUE, MONEY_MAX_VALUE),
                faker.number().numberBetween(111, MONEY_MAX_VALUE));
    }
}
