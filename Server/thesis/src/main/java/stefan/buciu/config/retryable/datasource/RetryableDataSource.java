package stefan.buciu.config.retryable.datasource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class RetryableDataSource extends AbstractDataSource {

    private final DataSource dataSource;

    @Override
    @Retryable(maxAttempts = 7, backoff = @Backoff(multiplier = 1.3, maxDelay = 100000))
    public Connection getConnection() throws SQLException {
        log.info("get connection");
        return dataSource.getConnection();
    }

    @Override
    @Retryable(maxAttempts = 7, backoff = @Backoff(multiplier = 1.3, maxDelay = 100000))
    public Connection getConnection(String username, String password) throws SQLException {
        log.info("get connection w/ username & password");
        return dataSource.getConnection(username, password);
    }
}
