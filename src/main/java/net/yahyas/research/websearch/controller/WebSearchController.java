package net.yahyas.research.websearch.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import net.yahyas.research.websearch.dto.WebSearchHistoryResponse;
import net.yahyas.research.websearch.dto.WritingAssistantRequest;
import net.yahyas.research.websearch.dto.WritingAssistantResponse;
import net.yahyas.research.websearch.service.WritingAssistantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/websearch")
public class WebSearchController {

    private final WritingAssistantService writingAssistantService;

    public WebSearchController(WritingAssistantService writingAssistantService) {
        this.writingAssistantService = writingAssistantService;
    }

    @PostMapping
    public WritingAssistantResponse search(
        @Valid @RequestBody WritingAssistantRequest request,
        Principal principal
    ) {
        return writingAssistantService.process(request, principal.getName(), true);
    }

    @PostMapping("/assist")
    public WritingAssistantResponse assist(
        @Valid @RequestBody WritingAssistantRequest request,
        Principal principal
    ) {
        WritingAssistantRequest assistRequest =
            new WritingAssistantRequest(request.text(), net.yahyas.research.websearch.model.WritingAction.ASSIST);
        return writingAssistantService.process(assistRequest, principal.getName(), true);
    }

    @GetMapping("/history")
    public List<WebSearchHistoryResponse> history(Principal principal) {
        return writingAssistantService.history(principal.getName());
    }
}
