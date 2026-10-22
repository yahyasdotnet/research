package net.yahyas.research.websearch.dto;

import java.time.Instant;
import java.util.List;
import net.yahyas.research.websearch.model.WritingAction;

public record WritingAssistantResponse(
    WritingAction action,
    String query,
    List<SearchSourceResponse> sources,
    String grammarSuggestion,
    String factCheck,
    Instant createdAt
) {
}
