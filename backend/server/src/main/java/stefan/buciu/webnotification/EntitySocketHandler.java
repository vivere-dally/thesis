package stefan.buciu.webnotification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import stefan.buciu.domain.exception.EntitySocketNotificationException;
import stefan.buciu.domain.model.Entity;
import stefan.buciu.domain.model.adapter.LocalDateTimeAdapter;
import stefan.buciu.domain.model.dto.DTO;
import stefan.buciu.domain.model.notification.Action;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class EntitySocketHandler extends TextWebSocketHandler {
    private final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final ReentrantLock reentrantLock = new ReentrantLock(true);
    public final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
            .serializeNulls()
            .create();

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        for (WebSocketSession webSocketSession : this.sessions.values()) {
            if (!webSocketSession.equals(session)) {
                webSocketSession.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        this.sessions.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        this.sessions.remove(userId);
    }

    public <E extends Entity<T>, T extends Serializable> void notifySessions(DTO<E, T> entity, Action action, long userId) throws EntitySocketNotificationException {
        if (!this.sessions.containsKey(userId)) {
            return;
        }

        var payload = new HashMap<String, Object>();
        payload.put("entity", entity);
        payload.put("actionType", action.toString());

        var textMessage = new TextMessage(gson.toJson(payload));

        try {
            reentrantLock.lock();
            this.sessions.get(userId).sendMessage(textMessage);
        } catch (IOException e) {
            throw new EntitySocketNotificationException(e);
        } finally {
            reentrantLock.unlock();
        }
    }
}
