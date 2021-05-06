package stefan.buciu.webnotification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class EntitySocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) {

        var path = serverHttpRequest.getURI().getPath();
        var userId = path.substring(path.lastIndexOf('/') + 1);

        try {
            map.put("userId", Long.parseLong(userId));
            return true;
        } catch (Exception ignored) {
            log.debug("No user id received");
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        /*
         * Nothing to do after the handshake
         * */
    }
}
