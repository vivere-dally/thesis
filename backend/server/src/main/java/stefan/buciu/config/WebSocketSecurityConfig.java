package stefan.buciu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import stefan.buciu.environment.AppSettings;
import stefan.buciu.webnotification.EntitySocketHandler;
import stefan.buciu.webnotification.EntitySocketInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketSecurityConfig implements WebSocketConfigurer {

    private final AppSettings appSettings;

    private final EntitySocketHandler entitySocketHandler;
    private final EntitySocketInterceptor entitySocketInterceptor;

    public WebSocketSecurityConfig(AppSettings appSettings, EntitySocketHandler entitySocketHandler, EntitySocketInterceptor entitySocketInterceptor) {
        this.appSettings = appSettings;
        this.entitySocketHandler = entitySocketHandler;
        this.entitySocketInterceptor = entitySocketInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(this.entitySocketHandler, "user/userId/*")
                .addInterceptors(entitySocketInterceptor)
                .setAllowedOrigins(appSettings.getSecurityCorsAllowedOrigins());
    }
}
