package stefan.buciu.config.retryable.datasource;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class RetryableDataSourcePostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            return new RetryableDataSource((DataSource) bean);
        }

        return bean;
    }
}
