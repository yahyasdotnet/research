package net.yahyas.research.websearch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import net.yahyas.research.websearch.model.WritingAction;

public record WritingAssistantRequest(
    @NotBlank @Size(max = 5000) String text,
    @NotNull WritingAction action
) {
}
