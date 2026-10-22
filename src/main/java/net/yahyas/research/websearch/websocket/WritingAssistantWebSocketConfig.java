package net.yahyas.research.websearch.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WritingAssistantWebSocketConfig implements WebSocketConfigurer {

    private final WritingAssistantWebSocketHandler handler;

    public WritingAssistantWebSocketConfig(WritingAssistantWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/writing-assistant");
    }
}
