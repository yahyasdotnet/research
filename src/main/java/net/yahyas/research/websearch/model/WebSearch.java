package net.yahyas.research.websearch.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "web_search")
@Getter
@Setter
@NoArgsConstructor
public class WebSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 320)
    private String ownerEmail;

    @Column(nullable = false, length = 500)
    private String query;

    @Column(nullable = false)
    private Instant createdAt;

    @ElementCollection
    @CollectionTable(name = "web_search_source", joinColumns = @JoinColumn(name = "search_id"))
    @OrderColumn(name = "position")
    private List<WebSearchSource> sources = new ArrayList<>();

    public WebSearch(String ownerEmail, String query, List<WebSearchSource> sources) {
        this.ownerEmail = ownerEmail;
        this.query = query;
        this.sources = new ArrayList<>(sources);
        this.createdAt = Instant.now();
    }
}
