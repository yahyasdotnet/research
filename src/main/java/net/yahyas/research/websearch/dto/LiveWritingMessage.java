package net.yahyas.research.websearch.dto;

import net.yahyas.research.websearch.model.WritingAction;

public record LiveWritingMessage(String token, String text, WritingAction action) {
}
