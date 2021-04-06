package stefan.buciu.environment;

public class SecurityConstants {
    public static final String KEY = "q3t6w9z$C&F)J@NcQfTjWnZr4u7x!A%D*G-KaPdSgUkXp2s5v8y/B?E(H+MbQeTh";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String ACCESS_TOKEN_HEADER = "accessToken";
    public static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 5;
    public static final String ACCESS_TOKEN_TYPE_HEADER = "tokenType";
    public static final String ACCESS_TOKEN_TYPE_HEADER_VALUE = "Bearer";

    public static final String REFRESH_TOKEN_HEADER = "refreshToken";
    public static final Long REFRESH_TOKEN_EXPIRATION_TIME = ACCESS_TOKEN_EXPIRATION_TIME * 3;

    private SecurityConstants() {
    }
}
