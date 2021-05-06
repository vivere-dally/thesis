package stefan.buciu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import stefan.buciu.environment.AppSettings;
import stefan.buciu.webnotification.EntitySocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketSecurityConfig implements WebSocketConfigurer {

    private final AppSettings appSettings;

    private final EntitySocketHandler entitySocketHandler;

    public WebSocketSecurityConfig(AppSettings appSettings, EntitySocketHandler entitySocketHandler) {
        this.appSettings = appSettings;
        this.entitySocketHandler = entitySocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(this.entitySocketHandler, "entity")
                .setAllowedOrigins(appSettings.getSecurityCorsAllowedOrigins());
    }
}
