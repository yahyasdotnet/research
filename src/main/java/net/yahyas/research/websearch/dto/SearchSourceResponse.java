package net.yahyas.research.websearch.dto;

import java.time.Instant;

public record SearchSourceResponse(String title, String url, String description, Instant publishedAt) {
}
