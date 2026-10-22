package net.yahyas.research.websearch.websocket;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.yahyas.research.websearch.dto.LiveWritingMessage;
import net.yahyas.research.websearch.dto.LiveWritingResponse;
import net.yahyas.research.websearch.dto.WritingAssistantRequest;
import net.yahyas.research.websearch.dto.WritingAssistantResponse;
import net.yahyas.research.websearch.model.WritingAction;
import net.yahyas.research.websearch.service.WritingAssistantService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WritingAssistantWebSocketHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WritingAssistantWebSocketHandler.class);
    private static final int MAX_TEXT_LENGTH = 5000;

    private final ObjectMapper objectMapper;
    private final JwtDecoder jwtDecoder;
    private final WritingAssistantService writingAssistantService;
    private final ScheduledExecutorService executor;
    private final Duration debounce;
    private final ConcurrentMap<String, SessionState> sessions = new ConcurrentHashMap<>();

    public WritingAssistantWebSocketHandler(
        ObjectMapper objectMapper,
        JwtDecoder jwtDecoder,
        WritingAssistantService writingAssistantService,
        ScheduledExecutorService executor,
        @Value("${app.writing-assistant.debounce:PT1S}") Duration debounce
    ) {
        if (debounce.isNegative()) {
            throw new IllegalArgumentException("Writing-assistant debounce must not be negative");
        }
        this.objectMapper = objectMapper;
        this.jwtDecoder = jwtDecoder;
        this.writingAssistantService = writingAssistantService;
        this.executor = executor;
        this.debounce = debounce;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        final LiveWritingMessage request;
        try {
            request = objectMapper.readValue(message.getPayload(), LiveWritingMessage.class);
        }
        catch (JacksonException exception) {
            send(session, new LiveWritingResponse("error", null, "Message must be valid JSON"));
            return;
        }

        SessionState state = sessions.get(session.getId());
        if (state == null) {
            authenticate(session, request);
            return;
        }

        if (request.text() == null || request.text().isBlank() || request.text().length() > MAX_TEXT_LENGTH) {
            send(session, new LiveWritingResponse("error", null,
                "Text must contain 1 to " + MAX_TEXT_LENGTH + " characters"));
            return;
        }

        WritingAction action = request.action() == null ? WritingAction.ASSIST : request.action();
        scheduleAnalysis(session, state, request.text(), action);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SessionState state = sessions.remove(session.getId());
        if (state != null) {
            state.cancelPending();
        }
    }

    private void authenticate(WebSocketSession session, LiveWritingMessage request) throws IOException {
        if (request.token() == null || request.token().isBlank()) {
            send(session, new LiveWritingResponse("error", null, "Send a valid JWT in the first message"));
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        try {
            Jwt jwt = jwtDecoder.decode(request.token());
            if (jwt.getSubject() == null || jwt.getSubject().isBlank()) {
                throw new JwtException("JWT subject is missing");
            }
            sessions.put(session.getId(), new SessionState(jwt.getSubject()));
            send(session, new LiveWritingResponse("authenticated", null, null));
        }
        catch (JwtException exception) {
            send(session, new LiveWritingResponse("error", null, "JWT is invalid or expired"));
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    private void scheduleAnalysis(
        WebSocketSession session,
        SessionState state,
        String text,
        WritingAction action
    ) throws IOException {
        synchronized (state) {
            long revision = state.revision.incrementAndGet();
            if (state.pending != null) {
                state.pending.cancel(false);
            }
            state.pending = executor.schedule(
                () -> analyze(session, state, revision, text, action),
                debounce.toMillis(),
                TimeUnit.MILLISECONDS);
        }
        send(session, new LiveWritingResponse("pending", null, null));
    }

    private void analyze(
        WebSocketSession session,
        SessionState state,
        long revision,
        String text,
        WritingAction action
    ) {
        try {
            WritingAssistantResponse result = writingAssistantService.process(
                new WritingAssistantRequest(text, action), state.ownerEmail, false);
            if (state.revision.get() == revision && session.isOpen()) {
                send(session, new LiveWritingResponse("result", result, null));
            }
        }
        catch (RuntimeException exception) {
            LOGGER.error("Live writing assistance failed for an authenticated session", exception);
            if (state.revision.get() == revision && session.isOpen()) {
                sendError(session);
            }
        }
        catch (IOException exception) {
            LOGGER.warn("Could not send a writing-assistant result to the WebSocket client", exception);
        }
    }

    private void sendError(WebSocketSession session) {
        try {
            send(session, new LiveWritingResponse("error", null,
                "Writing assistance is temporarily unavailable"));
        }
        catch (IOException exception) {
            LOGGER.warn("Could not send a writing-assistant error to the WebSocket client", exception);
        }
    }

    private void send(WebSocketSession session, LiveWritingResponse response) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    private static final class SessionState {
        private final String ownerEmail;
        private final AtomicLong revision = new AtomicLong();
        private ScheduledFuture<?> pending;

        private SessionState(String ownerEmail) {
            this.ownerEmail = ownerEmail;
        }

        private synchronized void cancelPending() {
            if (pending != null) {
                pending.cancel(false);
            }
        }
    }
}
