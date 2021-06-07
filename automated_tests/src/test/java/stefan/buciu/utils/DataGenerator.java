package stefan.buciu.utils;

import com.github.javafaker.Faker;

import java.util.regex.Pattern;

public class DataGenerator {
    private static final Faker faker = Faker.instance();
    private static final Pattern USR_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,16}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PSW_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})");

    public static int USR_MIN_LEN = 3, USR_MAX_LEN = 16;
    public static int PWD_MIN_LEN = 8, PWD_MAX_LEN = 50;
    public static int MONEY_MIN_VALUE = 11, MONEY_MAX_VALUE = 999;

    public static String password(boolean valid) {
        if (valid) {
            String psw;
            do {
                psw = faker.internet().password(PWD_MIN_LEN, PWD_MAX_LEN, true, true, true);
            } while (!PSW_PATTERN.matcher(psw).find());

            return psw;
        }

        return faker.internet().password(1, 7);
    }

    public static String username(boolean valid) {
        if (valid) {
            String usr;
            do {
                usr = faker.internet().password(USR_MIN_LEN, USR_MAX_LEN);
            } while (!USR_PATTERN.matcher(usr).find());

            return usr;
        }

        return faker.internet().password(1, 2);
    }

    public static String money(boolean valid) {
        if (valid) {
            return String.format("%d.%d%d",
                    faker.number().numberBetween(MONEY_MIN_VALUE, MONEY_MAX_VALUE),
                    faker.number().numberBetween(1, 9),
                    faker.number().numberBetween(1, 9));
        }

        return String.format("%d.%d",
                faker.number().numberBetween(MONEY_MIN_VALUE, MONEY_MAX_VALUE),
                faker.number().numberBetween(111, MONEY_MAX_VALUE));
    }

    public static String message() {
        return password(true);
    }
}
