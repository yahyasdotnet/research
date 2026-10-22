package net.yahyas.research.websearch.dto;

import java.time.Instant;
import java.util.List;

public record WebSearchHistoryResponse(
    Long id,
    String query,
    List<SearchSourceResponse> sources,
    Instant createdAt
) {
}
