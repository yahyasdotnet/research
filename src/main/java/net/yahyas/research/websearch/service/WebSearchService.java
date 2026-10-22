package net.yahyas.research.websearch.service;

import java.util.List;
import net.yahyas.research.websearch.dto.SearchSourceResponse;
import net.yahyas.research.websearch.model.WebSearchSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WebSearchService {

    private final RestClient restClient;
    private final String apiKey;
    private final int maxResults;

    public WebSearchService(
        RestClient.Builder restClientBuilder,
        @Value("${app.web-search.firecrawl-api-key:}") String apiKey,
        @Value("${app.web-search.max-results:5}") int maxResults
    ) {
        if (maxResults < 1 || maxResults > 20) {
            throw new IllegalArgumentException("Web search max-results must be between 1 and 20");
        }
        this.restClient = restClientBuilder.baseUrl("https://api.firecrawl.dev/v2").build();
        this.apiKey = apiKey;
        this.maxResults = maxResults;
    }

    public List<SearchSourceResponse> search(String query) {
        if (apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Web search is not configured; set FIRECRAWL_API_KEY");
        }

        try {
            FirecrawlSearchResponse response = restClient.post()
                .uri("/search")
                .header("Authorization", "Bearer " + apiKey)
                .body(new FirecrawlSearchRequest(query, maxResults, List.of("web")))
                .retrieve()
                .body(FirecrawlSearchResponse.class);

            if (response == null || !Boolean.TRUE.equals(response.success())
                || response.data() == null || response.data().web() == null) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Web search provider returned an invalid response");
            }

            return response.data().web().stream()
                .map(result -> new SearchSourceResponse(
                    result.title(),
                    result.url(),
                    result.description(),
                    null))
                .toList();
        }
        catch (ResponseStatusException exception) {
            throw exception;
        }
        catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Web search provider request failed", exception);
        }
    }

    public List<WebSearchSource> toEntities(List<SearchSourceResponse> results) {
        return results.stream()
            .map(result -> new WebSearchSource(
                result.title(), result.url(), result.description(), result.publishedAt()))
            .toList();
    }

    private record FirecrawlSearchRequest(String query, int limit, List<String> sources) {
    }

    private record FirecrawlSearchResponse(Boolean success, FirecrawlData data) {
    }

    private record FirecrawlData(List<FirecrawlResult> web) {
    }

    private record FirecrawlResult(String title, String url, String description) {
    }
}
