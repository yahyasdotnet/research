package net.yahyas.research.websearch.websocket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WritingAssistantExecutorConfig {

    @Bean(destroyMethod = "shutdown")
    ScheduledExecutorService writingAssistantExecutor() {
        return Executors.newScheduledThreadPool(4);
    }
}
